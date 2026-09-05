package hutian.basic.controller;

import baibao.common.dto.LongIdsDTO;
import hutian.basic.constant.UrlConstant;
import hutian.basic.pojo.entity.ChangeLog;
import hutian.basic.pojo.param.ChangeLogAddParam;
import hutian.basic.pojo.param.ChangeLogEditParam;
import hutian.basic.pojo.query.ChangeLogQuery;
import hutian.basic.pojo.result.ChangeLogResult;
import hutian.basic.pojo.result.ChangeLogSmpResult;
import hutian.basic.service.ChangeLogService;
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

/**
 * 数据变动日志表的前端控制器.
 *
 * @folder 基础服务/数据变动日志
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@RestController
@RequestMapping(UrlConstant.CHANGE_LOG)
public class ChangeLogController {

    @Resource
    private ChangeLogService changeLogService;

    /**
     * 数据变动日志-增加
     *
     * @param param 待增加的数据
     * @return 增加的记录的ID
     */
    @OperationLog(name = "数据变动日志-增加")
    @PostMapping("/add")
    public Result<Number> add(@RequestBody @Validated ChangeLogAddParam param) {

        return Result.success(changeLogService.addRecord(param));
    }

    /**
     * 数据变动日志-编辑
     *
     * @param param 待编辑的数据
     * @return 操作结果
     */
    @OperationLog(name = "数据变动日志-编辑")
    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody @Validated ChangeLogEditParam param) {
        changeLogService.editRecord(param);
        return Result.success();
    }

    /**
     * 数据变动日志-删除
     *
     * @param param 待删除的数据
     * @return 操作结果
     */
    @OperationLog(name = "数据变动日志-删除")
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody @Validated LongIdsDTO param) {
        changeLogService.deleteByLongIds(param.getIds());
        return Result.success();
    }

    /**
     * 数据变动日志-详情
     *
     * @param recordId 数据变动日志表的ID
     * @return 查询到的数据变动日志表详情
     */
    @OperationLog(name = "数据变动日志-详情")
    @GetMapping("/{recordId}")
    public Result<ChangeLogResult> detail(@PathVariable("recordId") Long recordId) {

        return Result.success(changeLogService.detailById(recordId));
    }

    /**
     * 数据变动日志-分页列表
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "数据变动日志-分页列表")
    @PostMapping("/query-page")
    public Result<Page<ChangeLogSmpResult>> queryPage(@RequestBody @Validated ChangeLogQuery query) {
        Page<ChangeLogResult> page = changeLogService.queryPage(query);
        return Result.success(PageUtil.handleResult(page, ChangeLogSmpResult.class));
    }

    /**
     * 数据变动日志-滚动分页
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "数据变动日志-滚动分页")
    @PostMapping("/scroll-page")
    public Result<Page<ChangeLogSmpResult>> scrollPage(@RequestBody @Validated ChangeLogQuery query) {
        Page<ChangeLogResult> page = changeLogService.queryScrollPage(query, ChangeLog::getId);
        return Result.success(PageUtil.handleResult(page, ChangeLogSmpResult.class));
    }

}
