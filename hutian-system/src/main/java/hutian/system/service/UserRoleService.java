package hutian.system.service;

import hutian.system.pojo.entity.UserRole;
import hutian.system.pojo.param.UserGrantRoleParam;
import hutian.system.pojo.result.RoleSmpResult;

import java.util.Collection;
import java.util.List;

/**
 * 用户角色表（用户与角色的授权绑定）的服务类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
public interface UserRoleService {

    /**
     * 全量覆盖式角色授权：事务内先逻辑删该用户现有的角色绑定，再插入新绑定.
     * <p>
     * 传入空集合表示清空该用户的所有角色授权。
     *
     * @param param 授权入参（userId + roleIds）
     */
    void grantRoles(UserGrantRoleParam param);

    /**
     * 根据用户ID查询已授权的角色ID集合（联 sys_user_role）.
     *
     * @param userId 用户ID
     * @return 已授权的角色ID集合
     */
    List<Long> findRoleIdsByUserId(Long userId);

    /**
     * 根据用户ID集合批量查询已授权的角色集合（联 sys_user_role + sys_role，后续安全上下文用）.
     *
     * @param userIds 用户ID集合
     * @return 查询到的角色集合（roleId 映射为 RoleSmpResult.id）
     */
    List<RoleSmpResult> findRolesByUserIds(Collection<Long> userIds);

}
