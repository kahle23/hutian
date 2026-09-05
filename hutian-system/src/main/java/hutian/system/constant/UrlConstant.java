package hutian.system.constant;

/**
 * URL 常量.
 * <p>
 * 与前端 api 层的路径保持一致，system 域新增 URL 统一在此登记.
 *
 * @author Hutian
 * @since 2026-09-06
 */
public interface UrlConstant {

    /** 模块统一前缀 - system（系统域：用户/角色/菜单/登录）. */
    String PREFIX = "/system";

    /** 登录. */
    String LOGIN = PREFIX + "/login";

    /** 登出. */
    String LOGOUT = PREFIX + "/logout";

    /** 当前用户信息. */
    String CURRENT = PREFIX + "/current";

    /** 用户管理. */
    String USER = PREFIX + "/user";

    /** 角色管理. */
    String ROLE = PREFIX + "/role";

    /** 菜单管理. */
    String MENU = PREFIX + "/menu";

}
