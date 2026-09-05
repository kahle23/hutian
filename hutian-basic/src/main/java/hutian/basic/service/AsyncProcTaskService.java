package hutian.basic.service;

import baibao.db.jdbc.mybatisplus.base.BaseService;
import hutian.basic.pojo.entity.AsyncProcTask;
import hutian.basic.pojo.param.AsyncProcTaskAddParam;
import hutian.basic.pojo.param.AsyncProcTaskEditParam;
import hutian.basic.pojo.query.AsyncProcTaskQuery;
import hutian.basic.pojo.result.AsyncProcTaskResult;

/**
 * 异步处理的任务表的服务类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
public interface AsyncProcTaskService extends BaseService<AsyncProcTask
        , AsyncProcTaskAddParam, AsyncProcTaskEditParam, AsyncProcTaskQuery, AsyncProcTaskResult> {

}
