package hutian.system.controller;

import hutian.system.constant.UrlConstant;
import hutian.system.pojo.param.LoginForm;
import hutian.system.pojo.result.CurrentUserResult;
import hutian.system.pojo.result.LoginResult;
import hutian.system.pojo.result.MenuTreeResult;
import hutian.system.service.LoginService;
import kunlun.common.Result;
import kunlun.core.annotation.OperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 系统登录与当前会话的前端控制器.
 *
 * @folder 系统管理/登录
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@RestController
@RequestMapping(UrlConstant.PREFIX)
public class LoginController {

    @Resource
    private LoginService loginService;

    /**
     * 系统登录（账号密码）.
     *
     * @param form 登录入参（account + password）
     * @return 登录结果（token）
     */
    @OperationLog(name = "系统-登录")
    @PostMapping("/login")
    public Result<LoginResult> login(@RequestBody @Validated LoginForm form) {

        return Result.success(loginService.login(form));
    }

    /**
     * 退出登录.
     *
     * @return 操作结果
     */
    @OperationLog(name = "系统-登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        loginService.logout();
        return Result.success();
    }

    /**
     * 当前登录人信息（id/account/name/avatar + roles）.
     *
     * @return 当前用户信息
     */
    @OperationLog(name = "系统-当前用户信息")
    @GetMapping("/current/info")
    public Result<CurrentUserResult> currentInfo() {

        return Result.success(loginService.currentUser());
    }

    /**
     * 当前登录人可见菜单树（enabled=1 且 show_flag=1，按角色聚合去重后组树）.
     *
     * @return 菜单树
     */
    @OperationLog(name = "系统-当前用户菜单树")
    @GetMapping("/current/menu-tree")
    public Result<List<MenuTreeResult>> currentMenuTree() {

        return Result.success(loginService.currentMenuTree());
    }

    /**
     * 当前登录人权限标识列表（去重）.
     *
     * @return 权限标识集合
     */
    @OperationLog(name = "系统-当前用户权限列表")
    @GetMapping("/current/permissions")
    public Result<Collection<String>> currentPermissions() {

        return Result.success(loginService.currentPermissions());
    }

}
