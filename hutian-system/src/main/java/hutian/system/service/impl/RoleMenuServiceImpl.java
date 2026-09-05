package hutian.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.base.MPJBaseServiceImpl;
import hutian.system.mapper.RoleMenuMapper;
import hutian.system.pojo.entity.RoleMenu;
import hutian.system.pojo.param.RoleGrantMenuParam;
import hutian.system.service.MenuService;
import hutian.system.service.RoleMenuService;
import kunlun.exception.util.VerifyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 角色菜单表（角色与菜单的授权绑定）的服务实现类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@Service
public class RoleMenuServiceImpl extends MPJBaseServiceImpl<RoleMenuMapper, RoleMenu>
        implements RoleMenuService {

    @Resource
    private MenuService menuService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void grantMenus(RoleGrantMenuParam param) {
        // 参数校验
        VerifyUtils.notNull(param, "待授权的角色菜单数据不能为空！");
        Long roleId = param.getRoleId();
        VerifyUtils.notNull(roleId, "角色ID不能为空！");
        // 待授权菜单ID集合（null 归一为空集合，表示清空）
        List<Long> menuIds = CollUtil.isNotEmpty(param.getMenuIds()) ? param.getMenuIds() : new ArrayList<>();
        // 1.逻辑删该角色现有的全部菜单绑定（全局逻辑删除自动过滤 delete_status=0 的查询，
        //   removeByIds 走逻辑删除，把 delete_status 置 1）
        List<RoleMenu> oldList = list(Wrappers.lambdaQuery(RoleMenu.class)
                .select(RoleMenu::getId)
                .eq(RoleMenu::getRoleId, roleId));
        if (CollUtil.isNotEmpty(oldList)) {
            List<Long> oldIds = new ArrayList<>();
            for (RoleMenu rm : oldList) {
                if (rm.getId() != null) {
                    oldIds.add(rm.getId());
                }
            }
            if (CollUtil.isNotEmpty(oldIds)) {
                removeByIds(oldIds);
            }
        }
        // 2.插入新的菜单绑定（普通索引，无唯一索引约束，重复授权场景亦可重建）
        if (CollUtil.isNotEmpty(menuIds)) {
            List<RoleMenu> willAdds = new ArrayList<>(menuIds.size());
            for (Long menuId : menuIds) {
                if (menuId == null) {
                    continue;
                }
                RoleMenu rm = new RoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                willAdds.add(rm);
            }
            if (CollUtil.isNotEmpty(willAdds)) {
                saveBatch(willAdds);
            }
        }
    }

    @Override
    public List<Long> findMenuIdsByRoleId(Long roleId) {
        // 参数校验
        VerifyUtils.notNull(roleId, "角色ID不能为空！");
        // 复用 MenuService.findMenuIdsByRoleIds（联 sys_role_menu，逻辑删除自动过滤）
        return menuService.findMenuIdsByRoleIds(Collections.singletonList(roleId));
    }

}
