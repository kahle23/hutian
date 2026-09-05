package hutian.system.service.impl;

import baibao.db.jdbc.mybatisplus.base.BaseServiceImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import hutian.system.mapper.MenuMapper;
import hutian.system.mapper.RoleMenuMapper;
import hutian.system.pojo.entity.Menu;
import hutian.system.pojo.entity.RoleMenu;
import hutian.system.pojo.param.MenuAddParam;
import hutian.system.pojo.param.MenuEditParam;
import hutian.system.pojo.query.MenuQuery;
import hutian.system.pojo.result.MenuResult;
import hutian.system.pojo.result.MenuTreeResult;
import hutian.system.service.MenuService;
import kunlun.data.bean.BeanUtil;
import kunlun.exception.util.VerifyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.util.StrUtil.isNotBlank;
import static java.util.Objects.nonNull;

/**
 * 菜单表（菜单兼权限点）的服务实现类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@Service
public class MenuServiceImpl extends BaseServiceImpl<MenuMapper, Menu
        , MenuAddParam, MenuEditParam, MenuQuery, MenuResult> implements MenuService {

    /**
     * 菜单类型：按钮
     */
    private static final int TYPE_BUTTON = 3;
    /**
     * 根菜单的父ID
     */
    private static final long ROOT_PARENT_ID = 0L;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    protected Menu fromAddParam(MenuAddParam param) {
        // 按钮类型必须有权限标识
        validatePermission(param.getType(), param.getPermission());
        // 默认值处理
        if (param.getParentId() == null) {
            param.setParentId(ROOT_PARENT_ID);
        }
        if (param.getSort() == null) {
            param.setSort(0);
        }
        // 转换成实体
        return BeanUtil.beanToBean(param, Menu.class);
    }

    @Override
    protected Menu fromEditParam(MenuResult old, MenuEditParam param) {
        // 按钮类型必须有权限标识
        validatePermission(param.getType(), param.getPermission());
        // 父级菜单不能是当前菜单
        VerifyUtils.isFalse(ObjUtil.equals(param.getId(), param.getParentId())
                , "父级菜单不能是当前菜单！");
        // 转换成实体
        return BeanUtil.beanToBean(param, Menu.class);
    }

    @Override
    protected MPJLambdaWrapper<Menu> buildQueryWrapper(MenuQuery query) {
        return JoinWrappers.lambda(Menu.class)
                .in(isNotEmpty(query.getIdList()), Menu::getId, query.getIdList())
                .in(isNotEmpty(query.getParentIdList()), Menu::getParentId, query.getParentIdList())
                .like(isNotBlank(query.getNameLike()), Menu::getName, query.getNameLike())
                .eq(nonNull(query.getType()), Menu::getType, query.getType())
                .eq(nonNull(query.getEnabled()), Menu::getEnabled, query.getEnabled())
                .eq(nonNull(query.getShowFlag()), Menu::getShowFlag, query.getShowFlag())
                .like(isNotBlank(query.getDescriptionLike()), Menu::getDescription, query.getDescriptionLike())
                .like(isNotBlank(query.getPermissionLike()), Menu::getPermission, query.getPermissionLike())
                .like(isNotBlank(query.getRouteLike()), Menu::getRoute, query.getRouteLike())
                .like(isNotBlank(query.getComponentLike()), Menu::getComponent, query.getComponentLike())
                // 排序：sort 升序、id 升序
                .orderByAsc(Menu::getSort)
                .orderByAsc(Menu::getId)
        ;
    }

    @Override
    protected void processData(MenuQuery query, List<MenuResult> data) {
        // 菜单列表暂无额外的数据处理
    }

    /**
     * 菜单变动日志暂不接入（待系统域统一接入变动日志体系）.
     */
    @Override
    protected void changeLog(Object bizId, Object oldData, Object newData, Class<?> targetClz, Object... arguments) {
    }

    @Override
    public List<MenuTreeResult> queryTree(MenuQuery query) {
        // 查询（已按 sort、id 升序，逻辑删除自动过滤）
        List<Menu> menus = list(buildQueryWrapper(query));
        if (CollUtil.isEmpty(menus)) {
            return new ArrayList<>();
        }
        // 转换成树节点
        List<MenuTreeResult> nodes = BeanUtil.beanToBeanInList(menus, MenuTreeResult.class);
        // 按 parentId 分组（保留顺序）
        Map<Long, List<MenuTreeResult>> byParent = new LinkedHashMap<>();
        for (MenuTreeResult node : nodes) {
            Long parentId = node.getParentId() == null ? ROOT_PARENT_ID : node.getParentId();
            byParent.computeIfAbsent(parentId, k -> new ArrayList<>()).add(node);
        }
        // 挂载子节点
        for (MenuTreeResult node : nodes) {
            List<MenuTreeResult> children = byParent.get(node.getId());
            if (CollUtil.isNotEmpty(children)) {
                node.setChildren(children);
            }
        }
        // 根节点
        return byParent.getOrDefault(ROOT_PARENT_ID, new ArrayList<>());
    }

    @Override
    public List<Long> findMenuIdsByRoleIds(Collection<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        // 联 sys_role_menu 查菜单ID（逻辑删除自动过滤 delete_status=0）
        List<RoleMenu> list = roleMenuMapper.selectList(Wrappers.lambdaQuery(RoleMenu.class)
                .select(RoleMenu::getMenuId)
                .in(RoleMenu::getRoleId, roleIds));
        return list.stream()
                .map(RoleMenu::getMenuId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByLongIds(Collection<Long> ids) {
        // 参数校验、去重
        VerifyUtils.notEmpty(ids, "待删除的ID集合不能为空！");
        Collection<Long> idList = CollUtil.distinct(ids);
        // 存在未删除子菜单则拒绝删除（全局逻辑删除自动过滤 delete_status=0）
        long childCount = count(Wrappers.lambdaQuery(Menu.class)
                .in(Menu::getParentId, idList));
        VerifyUtils.isTrue(childCount == 0, "存在子菜单，请先删除子菜单后再操作！");
        // 调用基类删除流程（逻辑删除）
        super.deleteByLongIds(idList);
    }

    /**
     * 校验按钮类型必须有权限标识.
     *
     * @param type       菜单类型
     * @param permission 权限标识
     */
    private void validatePermission(Integer type, String permission) {
        VerifyUtils.isFalse(TYPE_BUTTON == (type == null ? 0 : type) && !isNotBlank(permission)
                , "按钮类型（type=3）必须填写权限标识！");
    }

}
