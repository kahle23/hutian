package hutian.system.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import hutian.system.pojo.entity.User;
import hutian.system.service.UserService;
import kunlun.data.Dict;
import kunlun.security.UserDetail;
import kunlun.security.UserGroup;
import kunlun.security.support.AbstractUserService;
import kunlun.util.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static cn.hutool.core.util.StrUtil.isNotBlank;

/**
 * 洞天安全上下文的用户服务（kunlun UserService 的真实实现）.
 * <p>
 * 取代 hutian-starter 的占位 HutianUserService，成为工程内唯一的 UserService bean，
 * 供 kunlun SecurityAutoConfiguration 装配请求级安全上下文（操作日志/权限校验等）使用.
 * RBAC 链路：sys_user_role → sys_role_menu → sys_menu.permission，与 th ThUserService 同口径，
 * 剥离 th 的机构/岗位/临时用户/Redis 等不迁移部分.
 * <p>
 * 用户类型固定 0=普通用户（洞天不区分用户类型），无机构概念，getUserGroups 一律返回空.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@Component
public class SystemUserService implements kunlun.security.UserService {

    /**
     * 普通用户类型：洞天不区分用户类型，统一用 0
     */
    private static final long USER_TYPE_NORMAL = 0L;

    /**
     * 查询用户权限标识的 SQL（RBAC 链路：user_role → role_menu → menu.permission）.
     * 与 th ThUserService.getPermissions 同口径，表名/字段名与 hutian sys_* 表一致；
     * 逻辑删除与启用状态在 SQL 内显式过滤（NamedParameterJdbcTemplate 不走 MyBatis-Plus 全局逻辑删除）.
     */
    private static final String SQL_PERMISSIONS =
            "SELECT DISTINCT t1.`permission`\n" +
            "FROM sys_user_role ur\n" +
            "JOIN sys_role_menu rm ON rm.`role_id` = ur.`role_id` AND rm.`delete_status` = 0\n" +
            "JOIN sys_menu t1 ON t1.`id` = rm.`menu_id` AND t1.`delete_status` = 0 AND t1.`enabled` = 1\n" +
            "WHERE ur.`delete_status` = 0\n" +
            "  AND ur.`user_id` = :userId\n" +
            "  AND t1.`permission` IS NOT NULL\n" +
            "  AND t1.`permission` <> ''\n";

    @Resource
    private UserService userService;
    @Resource
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    /**
     * 查询用户详情，转为 kunlun UserDetail 供安全上下文使用.
     * <p>
     * 仅填充 UserDetail 接口字段（id/type/account/displayName/enabled），
     * 角色信息不在 UserDetail 范畴内（/system/current/info 接口单独返回 roles）.
     *
     * @param userId   用户ID
     * @param userType 用户类型（洞天统一 0，入参忽略）
     * @return 用户详情或 null
     */
    @Override
    public UserDetail getUserDetail(Object userId, Object userType) {
        // 用户ID判空
        if (userId == null) {
            return null;
        }
        Long userIdL = Convert.toLong(userId);
        if (userIdL == null) {
            return null;
        }
        // 查询用户信息（逻辑删除自动过滤 delete_status=0）
        User user = userService.getById(userIdL);
        if (user == null) {
            return null;
        }
        // 转换为 UserDetail（洞天统一 userType=0）；displayName 取 name，name 为空回退 account
        AbstractUserService.UserImpl detail = new AbstractUserService.UserImpl();
        detail.setId(user.getId());
        detail.setType(USER_TYPE_NORMAL);
        detail.setAccount(user.getAccount());
        detail.setDisplayName(isNotBlank(user.getName()) ? user.getName() : user.getAccount());
        detail.setEnabled(user.getEnabled() != null && user.getEnabled() == 1);
        return detail;
    }

    /**
     * 查询用户的权限标识集合（RBAC 链路聚合，去重）.
     * <p>
     * 走 NamedParameterJdbcTemplate 直查（不经过 MyBatis-Plus 全局逻辑删除），
     * SQL 内已对 delete_status / enabled 做显式过滤，并对 permission 去重去空.
     *
     * @param userId   用户ID
     * @param userType 用户类型（洞天统一 0，入参忽略）
     * @return 权限标识集合（去重，可能为空）
     */
    @Override
    public Collection<String> getPermissions(Object userId, Object userType) {
        Assert.notNull(userId, "Parameter \"userId\" must not null. ");
        Long userIdL = Convert.toLong(userId);
        if (userIdL == null) {
            return Collections.emptyList();
        }
        // 直查权限标识（SQL 已去重去空）
        List<String> permissions = namedJdbcTemplate.queryForList(
                SQL_PERMISSIONS, Dict.of().set("userId", userIdL), String.class);
        if (CollUtil.isEmpty(permissions)) {
            return Collections.emptyList();
        }
        // 兜底去重（SQL DISTINCT 已保证，此处防御性去重）
        Set<String> distinct = new LinkedHashSet<>(permissions);
        return Collections.unmodifiableCollection(distinct);
    }

    /**
     * 查询用户所属用户组.
     * <p>
     * 洞天无机构/部门概念，一律返回空集合.
     *
     * @param userId    用户ID
     * @param userType  用户类型
     * @param groupType 用户组类型
     * @return 空集合
     */
    @Override
    public Collection<String> getUserGroups(Object userId, Object userType, Object groupType) {
        // 洞天无机构概念
        return Collections.emptyList();
    }

    /**
     * 批量查询用户详情.
     * <p>
     * 简单按 getUserDetail 循环实现（洞天无 th 的机构/岗位数据填充与临时用户分支）.
     *
     * @param userIds   用户ID集合
     * @param userType  用户类型
     * @return 用户详情集合
     */
    @Override
    public Collection<UserDetail> getUserDetails(Collection<?> userIds, Object userType) {
        Collection<UserDetail> result = new ArrayList<>();
        if (CollUtil.isEmpty(userIds)) {
            return result;
        }
        // 去重后逐个查询
        List<Long> userIdsL = Convert.toList(Long.class, userIds);
        Set<Long> distinctIds = new LinkedHashSet<>(userIdsL);
        for (Long id : distinctIds) {
            UserDetail detail = getUserDetail(id, userType);
            if (detail != null) {
                result.add(detail);
            }
        }
        return result;
    }

    /**
     * 查询用户组信息（按用户组ID）.
     * <p>
     * 洞天无机构概念，一律返回空集合.
     *
     * @param groupIds  用户组ID集合
     * @param groupType 用户组类型
     * @return 空集合
     */
    @Override
    public Collection<UserGroup> getUserGroups(Collection<?> groupIds, Object groupType) {
        // 洞天无机构概念
        return Collections.emptyList();
    }

}
