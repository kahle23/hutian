package hutian.basic.controller;

import baibao.common.dto.LongIdsDTO;
import hutian.basic.constant.UrlConstant;
import hutian.basic.pojo.entity.OpLog;
import hutian.basic.pojo.param.OpLogAddParam;
import hutian.basic.pojo.param.OpLogEditParam;
import hutian.basic.pojo.query.OpLogQuery;
import hutian.basic.pojo.result.OpLogResult;
import hutian.basic.pojo.result.OpLogSmpResult;
import hutian.basic.service.OpLogService;
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
 * 操作日志表的前端控制器.
 *
 * @folder 基础服务/操作日志
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@RestController
@RequestMapping(UrlConstant.OP_LOG)
public class OpLogController {

    @Resource
    private OpLogService opLogService;

    /**
     * 操作日志-增加
     *
     * @param param 待增加的数据
     * @return 增加的记录的ID
     */
    @OperationLog(name = "操作日志-增加")
    @PostMapping("/add")
    public Result<Number> add(@RequestBody @Validated OpLogAddParam param) {

        return Result.success(opLogService.addRecord(param));
    }

    /**
     * 操作日志-编辑
     *
     * @param param 待编辑的数据
     * @return 操作结果
     */
    @OperationLog(name = "操作日志-编辑")
    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody @Validated OpLogEditParam param) {
        opLogService.editRecord(param);
        return Result.success();
    }

    /**
     * 操作日志-删除
     *
     * @param param 待删除的数据
     * @return 操作结果
     */
    @OperationLog(name = "操作日志-删除")
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody @Validated LongIdsDTO param) {
        opLogService.deleteByLongIds(param.getIds());
        return Result.success();
    }

    /**
     * 操作日志-详情
     *
     * @param recordId 操作日志表的ID
     * @return 查询到的操作日志表详情
     */
    @OperationLog(name = "操作日志-详情")
    @GetMapping("/{recordId}")
    public Result<OpLogResult> detail(@PathVariable("recordId") Long recordId) {

        return Result.success(opLogService.detailById(recordId));
    }

    /**
     * 操作日志-分页列表
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "操作日志-分页列表")
    @PostMapping("/query-page")
    public Result<Page<OpLogSmpResult>> queryPage(@RequestBody @Validated OpLogQuery query) {
        Page<OpLogResult> page = opLogService.queryPage(query);
        return Result.success(PageUtil.handleResult(page, OpLogSmpResult.class));
    }

    /**
     * 操作日志-滚动分页
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "操作日志-滚动分页")
    @PostMapping("/scroll-page")
    public Result<Page<OpLogSmpResult>> scrollPage(@RequestBody @Validated OpLogQuery query) {
        Page<OpLogResult> page = opLogService.queryScrollPage(query, OpLog::getId);
        return Result.success(PageUtil.handleResult(page, OpLogSmpResult.class));
    }

}
