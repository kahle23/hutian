package hutian.system.service;

import baibao.db.jdbc.mybatisplus.base.BaseService;
import hutian.system.pojo.entity.Menu;
import hutian.system.pojo.param.MenuAddParam;
import hutian.system.pojo.param.MenuEditParam;
import hutian.system.pojo.query.MenuQuery;
import hutian.system.pojo.result.MenuResult;
import hutian.system.pojo.result.MenuTreeResult;

import java.util.Collection;
import java.util.List;

/**
 * 菜单表（菜单兼权限点）的服务类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
public interface MenuService extends BaseService<Menu
        , MenuAddParam, MenuEditParam, MenuQuery, MenuResult> {

    /**
     * 按条件过滤后组装菜单树（不分页）.
     * <p>
     * 按 sort 升序、id 升序排序后组树，根节点 parentId = 0.
     *
     * @param query 查询条件
     * @return 菜单树
     */
    List<MenuTreeResult> queryTree(MenuQuery query);

    /**
     * 根据角色ID集合查询已授权的菜单ID集合（联 sys_role_menu）.
     *
     * @param roleIds 角色ID集合
     * @return 菜单ID集合
     */
    List<Long> findMenuIdsByRoleIds(Collection<Long> roleIds);

}
