package hutian.cms.service;

import baibao.db.jdbc.mybatisplus.base.BaseService;
import hutian.cms.enums.CmsArticleLogAction;
import hutian.cms.pojo.entity.ArticleLog;
import hutian.cms.pojo.param.ArticleLogAddParam;
import hutian.cms.pojo.param.ArticleLogEditParam;
import hutian.cms.pojo.query.ArticleLogQuery;
import hutian.cms.pojo.result.ArticleLogResult;

/**
 * 内容生命周期留痕表的服务类.
 *
 * @author Hutian
 * @since 2026-11-06
 */
public interface ArticleLogService extends BaseService<ArticleLog
        , ArticleLogAddParam, ArticleLogEditParam, ArticleLogQuery, ArticleLogResult> {

    /**
     * 写生命周期留痕（供本模块各服务内部调用）.
     * <p>
     * 操作人取当前登录人（定时任务场景无登录上下文，为 NULL）；动作时间=当前时间.
     *
     * @param articleId 内容ID
     * @param action    动作类型
     * @param detail    详情文案（可为空）
     */
    void writeLog(Long articleId, CmsArticleLogAction action, String detail);

}
