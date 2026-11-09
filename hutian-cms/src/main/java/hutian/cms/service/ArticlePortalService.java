package hutian.cms.service;

import hutian.cms.pojo.query.PortalQuery;
import hutian.cms.pojo.result.CategoryPortalListResult;
import hutian.cms.pojo.result.PortalResult;
import kunlun.common.Page;

import java.util.List;

/**
 * 内容用户端（门户）的服务类：仅已发布且有效期内，登录即可访问.
 *
 * @author Hutian
 * @since 2026-11-06
 */
public interface ArticlePortalService {

    /**
     * 门户分页（可见性口径：已发布 + 发布时间已到 + 未过有效期；置顶权重倒序 → 发布时间倒序）.
     *
     * @param query 查询条件（categoryId/typeList/keyword/fullText）
     * @return 门户分页（行含 isRead/snippet/categoryName/publishUserName）
     */
    Page<PortalResult> queryPage(PortalQuery query);

    /**
     * 门户详情（可见性校验 + 浏览数原子 +1 + 记已读（不存在才 insert）+ 返回全字段）.
     *
     * @param recordId 内容ID
     * @return 门户详情
     */
    PortalResult detail(Long recordId);

    /**
     * 最新公告（type=12，按发布时间倒序前 limit 条）.
     *
     * @param limit 条数（为空或 &lt;=0 时默认 5）
     * @return 最新公告列表
     */
    List<PortalResult> noticeLatest(Integer limit);

    /**
     * 当前用户公告(type=12)未读数（可见内容 notExists 已读表）.
     *
     * @return 未读数
     */
    Long unreadCount();

    /**
     * 门户分类文件夹列表（启用分类 GROUP BY 可见内容计数 + 总数 + 未分类数）.
     *
     * @param type 内容类型（必填，须为已注册的非缺省码值）
     * @return 分类文件夹列表
     */
    CategoryPortalListResult categoryList(Integer type);

    /**
     * 我的已读（已读表 JOIN 内容，readTime=首次阅读时间倒序）.
     *
     * @param query 查询条件
     * @return 我的已读分页
     */
    Page<PortalResult> myRead(PortalQuery query);

    /**
     * 我的未读（可见内容过滤未读，置顶权重倒序 → 发布时间倒序）.
     *
     * @param query 查询条件
     * @return 我的未读分页
     */
    Page<PortalResult> myUnread(PortalQuery query);

}
