package hutian.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import hutian.system.mapper.UserRoleMapper;
import hutian.system.pojo.entity.Role;
import hutian.system.pojo.entity.UserRole;
import hutian.system.pojo.param.UserGrantRoleParam;
import hutian.system.pojo.result.RoleSmpResult;
import hutian.system.service.UserRoleService;
import kunlun.exception.util.VerifyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户角色表（用户与角色的授权绑定）的服务实现类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@Service
public class UserRoleServiceImpl extends MPJBaseServiceImpl<UserRoleMapper, UserRole>
        implements UserRoleService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void grantRoles(UserGrantRoleParam param) {
        // 参数校验
        VerifyUtils.notNull(param, "待授权的用户角色数据不能为空！");
        Long userId = param.getUserId();
        VerifyUtils.notNull(userId, "用户ID不能为空！");
        // 待授权角色ID集合（null 归一为空集合，表示清空）
        List<Long> roleIds = CollUtil.isNotEmpty(param.getRoleIds()) ? param.getRoleIds() : new ArrayList<>();
        // 1.逻辑删该用户现有的全部角色绑定（全局逻辑删除自动过滤 delete_status=0 的查询，
        //   removeByIds 走逻辑删除，把 delete_status 置 1）
        List<UserRole> oldList = list(Wrappers.lambdaQuery(UserRole.class)
                .select(UserRole::getId)
                .eq(UserRole::getUserId, userId));
        if (CollUtil.isNotEmpty(oldList)) {
            List<Long> oldIds = oldList.stream()
                    .map(UserRole::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(oldIds)) {
                removeByIds(oldIds);
            }
        }
        // 2.插入新的角色绑定（普通索引，无唯一索引约束，重复授权场景亦可重建）
        if (CollUtil.isNotEmpty(roleIds)) {
            List<UserRole> willAdds = new ArrayList<>(roleIds.size());
            for (Long roleId : roleIds) {
                if (roleId == null) {
                    continue;
                }
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                willAdds.add(ur);
            }
            if (CollUtil.isNotEmpty(willAdds)) {
                saveBatch(willAdds);
            }
        }
    }

    @Override
    public List<Long> findRoleIdsByUserId(Long userId) {
        // 参数校验
        VerifyUtils.notNull(userId, "用户ID不能为空！");
        // 联 sys_user_role 查角色ID（逻辑删除自动过滤 delete_status=0）
        List<UserRole> list = list(Wrappers.lambdaQuery(UserRole.class)
                .select(UserRole::getRoleId)
                .eq(UserRole::getUserId, userId));
        return list.stream()
                .map(UserRole::getRoleId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleSmpResult> findRolesByUserIds(Collection<Long> userIds) {
        // 参数校验，去重
        VerifyUtils.notEmpty(userIds, "用户ID集合不能为空！");
        List<Long> distinctIds = CollUtil.distinct(userIds);
        // 联 sys_role 查角色（参考 th UserRoleServiceImpl.findRolesByUserIds，
        //   roleId 映射为 RoleSmpResult.id，逻辑删除自动过滤 delete_status=0 的绑定）
        return selectJoinList(RoleSmpResult.class, JoinWrappers.lambda(UserRole.class)
                .selectAs(UserRole::getRoleId, RoleSmpResult::getId)
                .selectAs(Role::getName, RoleSmpResult::getName)
                .selectAs(Role::getCode, RoleSmpResult::getCode)
                .selectAs(Role::getEnabled, RoleSmpResult::getEnabled)
                .in(UserRole::getUserId, distinctIds)
                .leftJoin(Role.class, Role::getId, UserRole::getRoleId)
        );
    }

}
