package hutian.system.service.impl;

import baibao.db.jdbc.mybatisplus.base.BaseServiceImpl;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import hutian.system.mapper.RoleMapper;
import hutian.system.mapper.UserRoleMapper;
import hutian.system.pojo.entity.Role;
import hutian.system.pojo.entity.UserRole;
import hutian.system.pojo.param.RoleAddParam;
import hutian.system.pojo.param.RoleEditParam;
import hutian.system.pojo.query.RoleQuery;
import hutian.system.pojo.result.RoleResult;
import hutian.system.service.RoleService;
import kunlun.data.bean.BeanUtil;
import kunlun.exception.util.VerifyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.util.StrUtil.isNotBlank;
import static java.util.Objects.nonNull;

/**
 * 角色表的服务实现类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@Service
public class RoleServiceImpl extends BaseServiceImpl<RoleMapper, Role
        , RoleAddParam, RoleEditParam, RoleQuery, RoleResult> implements RoleService {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    protected Role fromAddParam(RoleAddParam param) {
        // 角色名称唯一校验（同 delete_status=0 范围内）
        VerifyUtils.isFalse(existByName(param.getName(), null)
                , "该角色名称已存在！");
        // 转换成实体
        return BeanUtil.beanToBean(param, Role.class);
    }

    @Override
    protected Role fromEditParam(RoleResult old, RoleEditParam param) {
        // 角色名称唯一校验（排除自身）
        VerifyUtils.isFalse(existByName(param.getName(), param.getId())
                , "该角色名称已存在！");
        // 转换成实体
        return BeanUtil.beanToBean(param, Role.class);
    }

    @Override
    public boolean existByName(String name, Long id) {
        VerifyUtils.notBlank(name, "角色名称不能为空！");
        return count(Wrappers.lambdaQuery(Role.class)
                .eq(Role::getName, name)
                .ne(nonNull(id), Role::getId, id)
        ) > 0;
    }

    @Override
    protected MPJLambdaWrapper<Role> buildQueryWrapper(RoleQuery query) {
        return JoinWrappers.lambda(Role.class)
                .in(isNotEmpty(query.getIdList()), Role::getId, query.getIdList())
                .like(isNotBlank(query.getNameLike()), Role::getName, query.getNameLike())
                .like(isNotBlank(query.getCodeLike()), Role::getCode, query.getCodeLike())
                .like(isNotBlank(query.getDescriptionLike()), Role::getDescription, query.getDescriptionLike())
                .eq(nonNull(query.getEnabled()), Role::getEnabled, query.getEnabled())
                // 排序：id 升序
                .orderByAsc(Role::getId)
        ;
    }

    @Override
    protected void processData(RoleQuery query, List<RoleResult> data) {
        // 角色列表暂无额外的数据处理
    }

    /**
     * 角色变动日志暂不接入（待系统域统一接入变动日志体系）.
     */
    @Override
    protected void changeLog(Object bizId, Object oldData, Object newData, Class<?> targetClz, Object... arguments) {
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByLongIds(Collection<Long> ids) {
        // 参数校验、去重
        VerifyUtils.notEmpty(ids, "待删除的ID集合不能为空！");
        Collection<Long> idList = CollUtil.distinct(ids);
        // 角色仍被 sys_user_role 绑定则拒绝删除（逻辑删除自动过滤 delete_status=0）
        long bindCount = userRoleMapper.selectCount(Wrappers.lambdaQuery(UserRole.class)
                .in(UserRole::getRoleId, idList));
        VerifyUtils.isTrue(bindCount == 0, "存在已分配用户的角色，请先解绑用户后再删除！");
        // 调用基类删除流程（逻辑删除）
        super.deleteByLongIds(idList);
    }

}
