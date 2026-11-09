package hutian.cms.controller;

import baibao.common.dto.LongIdsDTO;
import hutian.cms.constant.UrlConstant;
import hutian.cms.pojo.param.CategoryAddParam;
import hutian.cms.pojo.param.CategoryEditParam;
import hutian.cms.pojo.param.CategoryEnableParam;
import hutian.cms.pojo.query.CategoryQuery;
import hutian.cms.pojo.result.CategoryResult;
import hutian.cms.pojo.result.CategorySmpResult;
import hutian.cms.service.CategoryService;
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
 * 内容分类的前端控制器（平铺，无父子树）.
 *
 * @folder 内容管理/分类
 * @author Hutian
 * @since 2026-11-06
 */
@Slf4j
@RestController
@RequestMapping(UrlConstant.CATEGORY)
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 分类-新增（code 非空时代码层唯一校验，未删除范围）
     *
     * @param param 待增加的数据
     * @return 增加的记录的ID
     */
    @OperationLog(name = "分类-新增")
    @PostMapping("/add")
    public Result<Number> add(@RequestBody @Validated CategoryAddParam param) {

        return Result.success(categoryService.addRecord(param));
    }

    /**
     * 分类-编辑（清空编码时提交 clearFields:["code"]）
     *
     * @param param 待编辑的数据
     * @return 操作结果
     */
    @OperationLog(name = "分类-编辑")
    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody @Validated CategoryEditParam param) {
        categoryService.editRecord(param);
        return Result.success();
    }

    /**
     * 分类-删除（分类下有未删内容禁止删）
     *
     * @param param 待删除的数据
     * @return 操作结果
     */
    @OperationLog(name = "分类-删除")
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody @Validated LongIdsDTO param) {
        categoryService.deleteByLongIds(param.getIds());
        return Result.success();
    }

    /**
     * 分类-批量启用/禁用
     *
     * @param param 启停入参（ids + enabled）
     * @return 操作结果
     */
    @OperationLog(name = "分类-启停")
    @PostMapping("/enable")
    public Result<Void> enable(@RequestBody @Validated CategoryEnableParam param) {
        categoryService.enable(param);
        return Result.success();
    }

    /**
     * 分类-详情
     *
     * @param recordId 分类的ID
     * @return 查询到的分类详情
     */
    @OperationLog(name = "分类-详情")
    @GetMapping("/{recordId}")
    public Result<CategoryResult> detail(@PathVariable("recordId") Long recordId) {

        return Result.success(categoryService.getById2(recordId));
    }

    /**
     * 分类-分页列表（排序 sort 升序 → id 倒序；含未删文章数 articleCount）
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "分类-分页列表")
    @PostMapping("/query-page")
    public Result<Page<CategorySmpResult>> queryPage(@RequestBody @Validated CategoryQuery query) {
        Page<CategoryResult> page = categoryService.queryPage(query);
        return Result.success(PageUtil.handleResult(page, CategorySmpResult.class));
    }

    /**
     * 分类-列表（同分页条件不分页）
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "分类-列表")
    @PostMapping("/query-list")
    public Result<List<CategorySmpResult>> queryList(@RequestBody @Validated CategoryQuery query) {
        List<CategoryResult> list = categoryService.queryList(query);
        return Result.success(PageUtil.handleResult(list, CategorySmpResult.class).getData());
    }

}
