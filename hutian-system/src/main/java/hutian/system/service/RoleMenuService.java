package hutian.system.service;

import hutian.system.pojo.entity.RoleMenu;
import hutian.system.pojo.param.RoleGrantMenuParam;

import java.util.List;

/**
 * 角色菜单表（角色与菜单的授权绑定）的服务类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
public interface RoleMenuService {

    /**
     * 全量覆盖式菜单授权：事务内先逻辑删该角色现有的菜单绑定，再插入新绑定.
     * <p>
     * 传入空集合表示清空该角色的所有菜单授权。
     *
     * @param param 授权入参（roleId + menuIds）
     */
    void grantMenus(RoleGrantMenuParam param);

    /**
     * 根据角色ID查询已授权的菜单ID集合（联 sys_role_menu）.
     *
     * @param roleId 角色ID
     * @return 已授权的菜单ID集合
     */
    List<Long> findMenuIdsByRoleId(Long roleId);

}
