package hutian.basic.service;

import baibao.db.jdbc.mybatisplus.base.BaseService;
import hutian.basic.pojo.entity.ChangeLog;
import hutian.basic.pojo.param.ChangeLogAddParam;
import hutian.basic.pojo.param.ChangeLogEditParam;
import hutian.basic.pojo.query.ChangeLogQuery;
import hutian.basic.pojo.result.ChangeLogResult;

/**
 * 数据变动日志表的服务类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
public interface ChangeLogService extends BaseService<ChangeLog
        , ChangeLogAddParam, ChangeLogEditParam, ChangeLogQuery, ChangeLogResult> {

}
