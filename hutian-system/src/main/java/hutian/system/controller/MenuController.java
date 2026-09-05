package hutian.system.controller;

import baibao.common.dto.LongIdsDTO;
import hutian.system.constant.UrlConstant;
import hutian.system.pojo.param.MenuAddParam;
import hutian.system.pojo.param.MenuEditParam;
import hutian.system.pojo.query.MenuQuery;
import hutian.system.pojo.result.MenuResult;
import hutian.system.pojo.result.MenuSmpResult;
import hutian.system.pojo.result.MenuTreeResult;
import hutian.system.service.MenuService;
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
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 菜单表（菜单兼权限点）的前端控制器.
 *
 * @folder 系统管理/菜单
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@RestController
@RequestMapping(UrlConstant.MENU)
public class MenuController {

    @Resource
    private MenuService menuService;

    /**
     * 菜单-增加
     *
     * @param param 待增加的数据
     * @return 增加的记录的ID
     */
    @OperationLog(name = "菜单-增加")
    @PostMapping("/add")
    public Result<Number> add(@RequestBody @Validated MenuAddParam param) {

        return Result.success(menuService.addRecord(param));
    }

    /**
     * 菜单-编辑
     *
     * @param param 待编辑的数据
     * @return 操作结果
     */
    @OperationLog(name = "菜单-编辑")
    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody @Validated MenuEditParam param) {
        menuService.editRecord(param);
        return Result.success();
    }

    /**
     * 菜单-删除
     *
     * @param param 待删除的数据
     * @return 操作结果
     */
    @OperationLog(name = "菜单-删除")
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody @Validated LongIdsDTO param) {
        menuService.deleteByLongIds(param.getIds());
        return Result.success();
    }

    /**
     * 菜单-详情
     *
     * @param recordId 菜单表的ID
     * @return 查询到的菜单表详情
     */
    @OperationLog(name = "菜单-详情")
    @GetMapping("/{recordId}")
    public Result<MenuResult> detail(@PathVariable("recordId") Long recordId) {

        return Result.success(menuService.detailById(recordId));
    }

    /**
     * 菜单-分页列表
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "菜单-分页列表")
    @PostMapping("/query-page")
    public Result<Page<MenuSmpResult>> queryPage(@RequestBody @Validated MenuQuery query) {
        Page<MenuResult> page = menuService.queryPage(query);
        return Result.success(PageUtil.handleResult(page, MenuSmpResult.class));
    }

    /**
     * 菜单-树查询（不分页）
     *
     * @param query 查询条件
     * @return 菜单树
     */
    @OperationLog(name = "菜单-树查询")
    @PostMapping("/query-tree")
    public Result<List<MenuTreeResult>> queryTree(@RequestBody @Validated MenuQuery query) {

        return Result.success(menuService.queryTree(query));
    }

}
