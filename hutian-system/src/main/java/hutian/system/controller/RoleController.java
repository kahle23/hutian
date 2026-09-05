package hutian.system.controller;

import baibao.common.dto.LongIdsDTO;
import hutian.system.constant.UrlConstant;
import hutian.system.pojo.param.RoleAddParam;
import hutian.system.pojo.param.RoleEditParam;
import hutian.system.pojo.param.RoleGrantMenuParam;
import hutian.system.pojo.query.RoleQuery;
import hutian.system.pojo.result.RoleResult;
import hutian.system.pojo.result.RoleSmpResult;
import hutian.system.service.RoleMenuService;
import hutian.system.service.RoleService;
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
 * 角色表的前端控制器.
 *
 * @folder 系统管理/角色
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@RestController
@RequestMapping(UrlConstant.ROLE)
public class RoleController {

    @Resource
    private RoleService roleService;

    @Resource
    private RoleMenuService roleMenuService;

    /**
     * 角色-增加
     *
     * @param param 待增加的数据
     * @return 增加的记录的ID
     */
    @OperationLog(name = "角色-增加")
    @PostMapping("/add")
    public Result<Number> add(@RequestBody @Validated RoleAddParam param) {

        return Result.success(roleService.addRecord(param));
    }

    /**
     * 角色-编辑
     *
     * @param param 待编辑的数据
     * @return 操作结果
     */
    @OperationLog(name = "角色-编辑")
    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody @Validated RoleEditParam param) {
        roleService.editRecord(param);
        return Result.success();
    }

    /**
     * 角色-删除
     *
     * @param param 待删除的数据
     * @return 操作结果
     */
    @OperationLog(name = "角色-删除")
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody @Validated LongIdsDTO param) {
        roleService.deleteByLongIds(param.getIds());
        return Result.success();
    }

    /**
     * 角色-详情
     *
     * @param recordId 角色表的ID
     * @return 查询到的角色表详情
     */
    @OperationLog(name = "角色-详情")
    @GetMapping("/{recordId}")
    public Result<RoleResult> detail(@PathVariable("recordId") Long recordId) {

        return Result.success(roleService.detailById(recordId));
    }

    /**
     * 角色-分页列表
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "角色-分页列表")
    @PostMapping("/query-page")
    public Result<Page<RoleSmpResult>> queryPage(@RequestBody @Validated RoleQuery query) {
        Page<RoleResult> page = roleService.queryPage(query);
        return Result.success(PageUtil.handleResult(page, RoleSmpResult.class));
    }

    /**
     * 角色-已授权菜单ID列表
     *
     * @param roleId 角色ID
     * @return 已授权的菜单ID集合
     */
    @OperationLog(name = "角色-已授权菜单ID列表")
    @GetMapping("/own-menu-ids")
    public Result<List<Long>> ownMenuIds(@RequestParam("roleId") Long roleId) {

        return Result.success(roleMenuService.findMenuIdsByRoleId(roleId));
    }

    /**
     * 角色-菜单授权保存（全量覆盖式授权）
     *
     * @param param 授权入参（roleId + menuIds）
     * @return 操作结果
     */
    @OperationLog(name = "角色-菜单授权保存")
    @PostMapping("/grant-menu")
    public Result<Void> grantMenu(@RequestBody @Validated RoleGrantMenuParam param) {
        roleMenuService.grantMenus(param);
        return Result.success();
    }

}
