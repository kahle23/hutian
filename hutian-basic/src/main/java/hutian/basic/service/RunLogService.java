package hutian.basic.service;

import baibao.db.jdbc.mybatisplus.base.BaseService;
import hutian.basic.pojo.entity.RunLog;
import hutian.basic.pojo.param.RunLogAddParam;
import hutian.basic.pojo.param.RunLogEditParam;
import hutian.basic.pojo.query.RunLogQuery;
import hutian.basic.pojo.result.RunLogResult;

/**
 * 运行日志表的服务类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
public interface RunLogService extends BaseService<RunLog
        , RunLogAddParam, RunLogEditParam, RunLogQuery, RunLogResult> {

}
