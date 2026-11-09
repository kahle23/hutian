package hutian.cms.service;

import baibao.db.jdbc.mybatisplus.base.BaseService;
import hutian.cms.pojo.entity.Article;
import hutian.cms.pojo.param.ArticleAddParam;
import hutian.cms.pojo.param.ArticleEditParam;
import hutian.cms.pojo.param.ArticlePublishParam;
import hutian.cms.pojo.param.ArticleTopParam;
import hutian.cms.pojo.query.ArticleQuery;
import hutian.cms.pojo.query.ArticleReadQuery;
import hutian.cms.pojo.result.ArticleResult;
import hutian.cms.pojo.result.ReadStatResult;
import hutian.cms.pojo.result.ReadUserResult;
import kunlun.common.Page;

import java.util.List;

/**
 * 内容表的服务类.
 *
 * @author Hutian
 * @since 2026-11-06
 */
public interface ArticleService extends BaseService<Article
        , ArticleAddParam, ArticleEditParam, ArticleQuery, ArticleResult> {

    /**
     * 发布（立即发布/定时发布/重新发布三合一）.
     * <p>
     * publishTime 为未来时间=定时发布（保持草稿仅登记到点时间）；立即/重新发布支持草稿(0)与已下线(2)，
     * 已发布(1)拦截报错；逐条留痕 PUBLISH.
     *
     * @param param 发布入参（ids 必填，publishTime/expireTime 可选）
     */
    void publish(ArticlePublishParam param);

    /**
     * 批量下线（仅已发布可下线，置 status=2，留痕 OFFLINE）.
     *
     * @param ids 内容ID集合
     */
    void offline(List<Long> ids);

    /**
     * 置顶/取消置顶（topWeight 0=取消置顶，>0 置顶，负数拦截；留痕 TOP/CANCEL_TOP）.
     *
     * @param param 置顶入参（id + topWeight）
     */
    void top(ArticleTopParam param);

    /**
     * 复制为草稿（标题+"（副本）"，清置顶/发布/到期/浏览数；留痕 COPY+CREATE；返回新 id）.
     *
     * @param recordId 源内容ID
     * @return 新内容的ID
     */
    Long copy(Long recordId);

    /**
     * 定时发布到点扫描：草稿且 publish_time&lt;=now 的内容批量置为已发布并留痕
     * （定时任务上下文无登录人，publishUser 保持为空）.
     *
     * @return 本次发布的条数
     */
    int publishDue();

    /**
     * 到期下线扫描：已发布且 expire_time&lt;=now 的内容批量下线（复用 offline）.
     *
     * @return 本次下线的条数
     */
    int expireDue();

    /**
     * 触达统计：目标人数（启用用户数）/已读人数/未读人数/触达率（1 位小数）.
     *
     * @param recordId 内容ID
     * @return 触达统计结果
     */
    ReadStatResult readStat(Long recordId);

    /**
     * 已读名单（cms_article_read 按文章关联用户，readTime 倒序分页）.
     *
     * @param recordId 内容ID
     * @param query    分页入参
     * @return 已读名单分页
     */
    Page<ReadUserResult> readUsers(Long recordId, ArticleReadQuery query);

    /**
     * 未读名单（启用用户 LEFT JOIN 已读表 IS NULL 过滤，按用户 id 升序分页）.
     *
     * @param recordId 内容ID
     * @param query    分页入参
     * @return 未读名单分页
     */
    Page<ReadUserResult> unreadUsers(Long recordId, ArticleReadQuery query);

}
