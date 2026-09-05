package hutian.basic.controller;

import baibao.common.dto.LongIdsDTO;
import hutian.basic.constant.UrlConstant;
import hutian.basic.pojo.entity.RunLog;
import hutian.basic.pojo.param.RunLogAddParam;
import hutian.basic.pojo.param.RunLogEditParam;
import hutian.basic.pojo.query.RunLogQuery;
import hutian.basic.pojo.result.RunLogResult;
import hutian.basic.pojo.result.RunLogSmpResult;
import hutian.basic.service.RunLogService;
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
 * 运行日志表的前端控制器.
 *
 * @folder 基础服务/运行日志
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@RestController
@RequestMapping(UrlConstant.RUN_LOG)
public class RunLogController {

    @Resource
    private RunLogService runLogService;

    /**
     * 运行日志-增加
     *
     * @param param 待增加的数据
     * @return 增加的记录的ID
     */
    @OperationLog(name = "运行日志-增加")
    @PostMapping("/add")
    public Result<Number> add(@RequestBody @Validated RunLogAddParam param) {

        return Result.success(runLogService.addRecord(param));
    }

    /**
     * 运行日志-编辑
     *
     * @param param 待编辑的数据
     * @return 操作结果
     */
    @OperationLog(name = "运行日志-编辑")
    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody @Validated RunLogEditParam param) {
        runLogService.editRecord(param);
        return Result.success();
    }

    /**
     * 运行日志-删除
     *
     * @param param 待删除的数据
     * @return 操作结果
     */
    @OperationLog(name = "运行日志-删除")
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody @Validated LongIdsDTO param) {
        runLogService.deleteByLongIds(param.getIds());
        return Result.success();
    }

    /**
     * 运行日志-详情
     *
     * @param recordId 运行日志表的ID
     * @return 查询到的运行日志表详情
     */
    @OperationLog(name = "运行日志-详情")
    @GetMapping("/{recordId}")
    public Result<RunLogResult> detail(@PathVariable("recordId") Long recordId) {

        return Result.success(runLogService.detailById(recordId));
    }

    /**
     * 运行日志-分页列表
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "运行日志-分页列表")
    @PostMapping("/query-page")
    public Result<Page<RunLogSmpResult>> queryPage(@RequestBody @Validated RunLogQuery query) {
        Page<RunLogResult> page = runLogService.queryPage(query);
        return Result.success(PageUtil.handleResult(page, RunLogSmpResult.class));
    }

    /**
     * 运行日志-滚动分页
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "运行日志-滚动分页")
    @PostMapping("/scroll-page")
    public Result<Page<RunLogSmpResult>> scrollPage(@RequestBody @Validated RunLogQuery query) {
        Page<RunLogResult> page = runLogService.queryScrollPage(query, RunLog::getId);
        return Result.success(PageUtil.handleResult(page, RunLogSmpResult.class));
    }

}
