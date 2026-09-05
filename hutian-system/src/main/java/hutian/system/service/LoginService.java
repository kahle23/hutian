package hutian.system.service;

import hutian.system.pojo.param.LoginForm;
import hutian.system.pojo.result.CurrentUserResult;
import hutian.system.pojo.result.LoginResult;
import hutian.system.pojo.result.MenuTreeResult;

import java.util.Collection;
import java.util.List;

/**
 * 系统登录的服务类.
 * <p>
 * 登录/登出与当前登录人会话相关的统一出口（鉴权链路核心收口）.
 *
 * @author Hutian
 * @since 2026-08-26
 */
public interface LoginService {

    /**
     * 系统登录（账号密码）.
     * <p>
     * 账号不存在与密码错误对外统一报"用户名或密码错误！"，不泄露账号是否存在；
     * 密码摘要比对走 kunlun CryptoUtil.digestToHex，与种子同算法.
     *
     * @param form 登录入参（account + password）
     * @return 登录结果（token）
     */
    LoginResult login(LoginForm form);

    /**
     * 退出登录.
     * <p>
     * 取当前请求 token 并 deleteToken（type=2 退出登录）.
     */
    void logout();

    /**
     * 当前登录人信息（id/account/name/avatar + roles）.
     *
     * @return 当前用户信息
     */
    CurrentUserResult currentUser();

    /**
     * 当前登录人可见菜单树（enabled=1 且 show_flag=1，按角色聚合去重后组树）.
     *
     * @return 菜单树
     */
    List<MenuTreeResult> currentMenuTree();

    /**
     * 当前登录人权限标识列表（去重）.
     *
     * @return 权限标识集合
     */
    Collection<String> currentPermissions();

}
