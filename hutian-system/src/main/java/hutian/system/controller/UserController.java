package hutian.system.controller;

import baibao.common.dto.LongIdsDTO;
import hutian.system.constant.UrlConstant;
import hutian.system.pojo.param.UserAddParam;
import hutian.system.pojo.param.UserEditParam;
import hutian.system.pojo.param.UserGrantRoleParam;
import hutian.system.pojo.param.UserResetPwdParam;
import hutian.system.pojo.param.UserUpdatePwdParam;
import hutian.system.pojo.query.UserQuery;
import hutian.system.pojo.result.UserResult;
import hutian.system.pojo.result.UserSmpResult;
import hutian.system.service.UserRoleService;
import hutian.system.service.UserService;
import kunlun.common.Page;
import kunlun.common.Result;
import kunlun.core.annotation.OperationLog;
import kunlun.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户表的前端控制器.
 *
 * @folder 系统管理/用户
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@RestController
@RequestMapping(UrlConstant.USER)
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserRoleService userRoleService;

    /**
     * 用户-增加
     *
     * @param param 待增加的数据
     * @return 增加的记录的ID
     */
    @OperationLog(name = "用户-增加")
    @PostMapping("/add")
    public Result<Number> add(@RequestBody @Validated UserAddParam param) {

        return Result.success(userService.addRecord(param));
    }

    /**
     * 用户-编辑
     *
     * @param param 待编辑的数据
     * @return 操作结果
     */
    @OperationLog(name = "用户-编辑")
    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody @Validated UserEditParam param) {
        userService.editRecord(param);
        return Result.success();
    }

    /**
     * 用户-删除
     *
     * @param param 待删除的数据
     * @return 操作结果
     */
    @OperationLog(name = "用户-删除")
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody @Validated LongIdsDTO param) {
        userService.deleteByLongIds(param.getIds());
        return Result.success();
    }

    /**
     * 用户-详情
     *
     * @param recordId 用户表的ID
     * @return 查询到的用户表详情（不含密码）
     */
    @OperationLog(name = "用户-详情")
    @GetMapping("/{recordId}")
    public Result<UserResult> detail(@PathVariable("recordId") Long recordId) {

        return Result.success(userService.detailById(recordId));
    }

    /**
     * 用户-分页列表
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "用户-分页列表")
    @PostMapping("/query-page")
    public Result<Page<UserSmpResult>> queryPage(@RequestBody @Validated UserQuery query) {
        Page<UserResult> page = userService.queryPage(query);
        return Result.success(PageUtil.handleResult(page, UserSmpResult.class));
    }

    /**
     * 用户-重置密码（管理员重置）
     *
     * @param param 重置密码入参（id + newPassword，为空用默认密码 hutian@2026）
     * @return 操作结果
     */
    @OperationLog(name = "用户-重置密码")
    @PostMapping("/reset-pwd")
    public Result<Void> resetPwd(@RequestBody @Validated UserResetPwdParam param) {
        userService.resetPwd(param);
        return Result.success();
    }

    /**
     * 用户-修改密码（本人修改）
     *
     * @param param 修改密码入参（oldPassword + newPassword）
     * @return 操作结果
     */
    @OperationLog(name = "用户-修改密码")
    @PostMapping("/update-pwd")
    public Result<Void> updatePwd(@RequestBody @Validated UserUpdatePwdParam param) {
        userService.updatePwd(param);
        return Result.success();
    }

    /**
     * 用户-角色授权保存（全量覆盖式授权）
     *
     * @param param 授权入参（userId + roleIds）
     * @return 操作结果
     */
    @OperationLog(name = "用户-角色授权保存")
    @PostMapping("/grant-role")
    public Result<Void> grantRole(@RequestBody @Validated UserGrantRoleParam param) {
        userRoleService.grantRoles(param);
        return Result.success();
    }

    /**
     * 用户-已授权角色ID列表
     *
     * @param userId 用户ID
     * @return 已授权的角色ID集合
     */
    @OperationLog(name = "用户-已授权角色ID列表")
    @GetMapping("/own-role-ids")
    public Result<List<Long>> ownRoleIds(@RequestParam("userId") Long userId) {

        return Result.success(userRoleService.findRoleIdsByUserId(userId));
    }

}
