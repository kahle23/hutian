package hutian.basic.service;

import baibao.db.jdbc.mybatisplus.base.BaseService;
import hutian.basic.pojo.entity.OpLog;
import hutian.basic.pojo.param.OpLogAddParam;
import hutian.basic.pojo.param.OpLogEditParam;
import hutian.basic.pojo.query.OpLogQuery;
import hutian.basic.pojo.result.OpLogResult;

/**
 * 操作日志表的服务类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
public interface OpLogService extends BaseService<OpLog
        , OpLogAddParam, OpLogEditParam, OpLogQuery, OpLogResult> {

}
