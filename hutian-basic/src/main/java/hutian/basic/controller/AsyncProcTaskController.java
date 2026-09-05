package hutian.basic.controller;

import hutian.basic.constant.UrlConstant;
import hutian.basic.pojo.entity.AsyncProcTask;
import hutian.basic.pojo.query.AsyncProcTaskQuery;
import hutian.basic.pojo.result.AsyncProcTaskResult;
import hutian.basic.pojo.result.AsyncProcTaskSmpResult;
import hutian.basic.service.AsyncProcTaskService;
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
 * 异步处理的任务表的前端控制器.
 * <p>
 * 任务由业务代码创建和驱动，对外只提供查询能力.
 *
 * @folder 基础服务/异步任务
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@RestController
@RequestMapping(UrlConstant.ASYNC_PROC_TASK)
public class AsyncProcTaskController {

    @Resource
    private AsyncProcTaskService asyncProcTaskService;

    /**
     * 异步任务-详情
     *
     * @param recordId 异步处理的任务表的ID
     * @return 查询到的异步处理的任务表详情
     */
    @OperationLog(name = "异步任务-详情")
    @GetMapping("/{recordId}")
    public Result<AsyncProcTaskResult> detail(@PathVariable("recordId") Long recordId) {

        return Result.success(asyncProcTaskService.detailById(recordId));
    }

    /**
     * 异步任务-分页列表
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "异步任务-分页列表")
    @PostMapping("/query-page")
    public Result<Page<AsyncProcTaskSmpResult>> queryPage(@RequestBody @Validated AsyncProcTaskQuery query) {
        Page<AsyncProcTaskResult> page = asyncProcTaskService.queryPage(query);
        return Result.success(PageUtil.handleResult(page, AsyncProcTaskSmpResult.class));
    }

    /**
     * 异步任务-滚动分页
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "异步任务-滚动分页")
    @PostMapping("/scroll-page")
    public Result<Page<AsyncProcTaskSmpResult>> scrollPage(@RequestBody @Validated AsyncProcTaskQuery query) {
        Page<AsyncProcTaskResult> page = asyncProcTaskService.queryScrollPage(query, AsyncProcTask::getId);
        return Result.success(PageUtil.handleResult(page, AsyncProcTaskSmpResult.class));
    }

}
