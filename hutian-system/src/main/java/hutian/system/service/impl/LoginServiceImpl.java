package hutian.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import hutian.system.component.SystemUserService;
import hutian.system.pojo.entity.User;
import hutian.system.pojo.param.LoginForm;
import hutian.system.pojo.query.MenuQuery;
import hutian.system.pojo.result.CurrentUserResult;
import hutian.system.pojo.result.LoginResult;
import hutian.system.pojo.result.MenuTreeResult;
import hutian.system.pojo.result.RoleSmpResult;
import hutian.system.service.LoginService;
import hutian.system.service.MenuService;
import hutian.system.service.UserService;
import hutian.system.service.UserRoleService;
import kunlun.action.ActionUtil;
import kunlun.common.constant.Nil;
import kunlun.crypto.CryptoUtil;
import kunlun.data.Event;
import kunlun.exception.ExceptionUtil;
import kunlun.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Numbers.TWO;
import static kunlun.exception.util.VerifyUtils.notBlank;
import static kunlun.exception.util.VerifyUtils.notNull;
import static kunlun.security.support.AbstractTokenManager.TokenImpl;

/**
 * 系统登录的服务实现类.
 * <p>
 * login/logout 主干保持精简：不含验证码/Redis/临时用户/oneOffToken/
 * 测试环境万能密码等附加机制. token 由 kunlun SecurityUtil.buildToken 产出（JWT，无状态，
 * TokenManager 由 hutian-boot HutianInfraConfig 注册的 JwtTokenManager 提供，30 天有效期）.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    /**
     * 普通用户类型：壶天不区分用户类型，统一用 0
     */
    private static final long USER_TYPE_NORMAL = 0L;

    @Resource
    private UserService userService;
    @Resource
    private MenuService menuService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private SystemUserService systemUserService;

    @Override
    public LoginResult login(LoginForm form) {
        Event event = Event.ofOperationLog().appendMessage("系统登录")
                .putData("account", form.getAccount());
        try {
            // 账号/密码核心校验（统一报错文案，不区分账号不存在/密码错误——干净版安全选择）
            User user = validateAccountPassword(form, event);
            Long userId = user.getId();
            // 构建 Token 信息（JWT，userType=0 普通用户）
            String token = SecurityUtil.buildToken(new TokenImpl(userId, USER_TYPE_NORMAL));
            // 本次请求的操作日志上下文（platform 取当前，tenant 留空）
            SecurityUtil.putBaseData(userId, USER_TYPE_NORMAL, SecurityUtil.getPlatform(), Nil.STR);
            // 构建结果
            LoginResult result = new LoginResult();
            result.setToken(token);
            return result;
        } catch (Exception e) {
            event.setLevel(Event.Level.ERROR).appendError(e.getMessage());
            throw ExceptionUtil.wrap(e);
        } finally {
            ActionUtil.execute(event);
        }
    }

    /**
     * 账号/密码核心校验.
     * <p>
     * 账号不存在与密码错误对外统一报"用户名或密码错误！"，不泄露账号是否存在（安全选择，注释说明）.
     *
     * @param form  登录入参
     * @param event 操作日志（查到用户后写 userId）
     * @return 校验通过的用户实体
     */
    private User validateAccountPassword(LoginForm form, Event event) {
        String account = form.getAccount().trim();
        String password = form.getPassword().trim();
        // 查询用户信息（逻辑删除自动过滤 delete_status=0）
        User user = userService.findByAccount(account);
        // 账号不存在统一报"用户名或密码错误！"（不区分账号是否存在）
        notNull(user, "用户名或密码错误！");
        // 操作日志写 userId
        if (event != null) {
            event.setUserId(user.getId());
        }
        // 用户是否启用
        notNull(user.getId(), "用户ID不能为空！");
        notBlank(user.getPassword(), "用户账号未设置密码！");
        // 账号被禁用可以明示
        if (user.getEnabled() == null || user.getEnabled() != ONE) {
            throw new IllegalArgumentException("用户账号已被禁用！");
        }
        // 密码摘要比对（与种子同算法：kunlun CryptoUtil.digestToHex）
        if (!StrUtil.equals(user.getPassword(), CryptoUtil.digestToHex(password))) {
            // 统一报错文案，不区分账号/密码错误
            throw new IllegalArgumentException("用户名或密码错误！");
        }
        return user;
    }

    @Override
    public void logout() {
        Event event = Event.ofOperationLog().appendMessage("系统登出");
        try {
            // 获取当前登陆人 Token
            String token = SecurityUtil.getToken();
            notBlank(token, "请传入当前登陆人的Token！");
            // 移除 Token（1 预留，2 退出登录）
            SecurityUtil.deleteToken(token, TWO);
        } catch (Exception e) {
            event.setLevel(Event.Level.ERROR).appendError(e.getMessage());
            throw ExceptionUtil.wrap(e);
        } finally {
            ActionUtil.execute(event);
        }
    }

    @Override
    public CurrentUserResult currentUser() {
        // 当前登录人 userId（不信任入参）
        Long userId = SecurityUtil.getUserId((Long) null);
        notNull(userId, "请先登录！");
        // 查询用户信息
        User user = userService.getById(userId);
        notNull(user, "当前用户信息不存在！");
        // 构建结果
        CurrentUserResult result = new CurrentUserResult();
        result.setId(user.getId());
        result.setAccount(user.getAccount());
        result.setName(user.getName());
        result.setAvatar(user.getAvatar());
        // 已授权角色（联 sys_user_role + sys_role，roleId 映射为 RoleSmpResult.id）
        List<RoleSmpResult> roles = userRoleService.findRolesByUserIds(singletonList(userId));
        result.setRoles(roles != null ? roles : new ArrayList<>());
        return result;
    }

    @Override
    public List<MenuTreeResult> currentMenuTree() {
        // 当前登录人 userId
        Long userId = SecurityUtil.getUserId((Long) null);
        notNull(userId, "请先登录！");
        // RBAC 链路聚合：用户角色 → 角色菜单 → 菜单（去重）
        List<Long> roleIds = userRoleService.findRoleIdsByUserId(userId);
        if (CollUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        List<Long> menuIds = menuService.findMenuIdsByRoleIds(roleIds);
        if (CollUtil.isEmpty(menuIds)) {
            return new ArrayList<>();
        }
        // 复用 Menu 域树组装：仅取 enabled=1 且 show_flag=1 的可见菜单
        MenuQuery query = new MenuQuery();
        query.setIdList(menuIds);
        query.setEnabled(ONE);
        query.setShowFlag(ONE);
        return menuService.queryTree(query);
    }

    @Override
    public Collection<String> currentPermissions() {
        // 当前登录人 userId
        Long userId = SecurityUtil.getUserId((Long) null);
        notNull(userId, "请先登录！");
        // 走 SystemUserService 的 RBAC SQL（已去重）
        Collection<String> permissions = systemUserService.getPermissions(userId, USER_TYPE_NORMAL);
        return permissions != null ? permissions : Collections.emptyList();
    }

}
