package hutian.cms.service.impl;

import baibao.common.dto.FileDTO;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import hutian.cms.enums.CmsArticleStatus;
import hutian.cms.enums.CmsArticleType;
import hutian.cms.mapper.ArticleReadMapper;
import hutian.cms.mapper.CategoryMapper;
import hutian.cms.mapper.SysUserMapper;
import hutian.cms.pojo.entity.Article;
import hutian.cms.pojo.entity.ArticleRead;
import hutian.cms.pojo.entity.Category;
import hutian.cms.pojo.entity.SysUser;
import hutian.cms.pojo.query.PortalQuery;
import hutian.cms.pojo.result.CategoryPortalListResult;
import hutian.cms.pojo.result.CategoryPortalResult;
import hutian.cms.pojo.result.PortalResult;
import hutian.cms.service.ArticlePortalService;
import hutian.cms.service.ArticleService;
import hutian.cms.util.CmsTextUtil;
import kunlun.common.Page;
import kunlun.data.bean.BeanUtil;
import kunlun.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.util.StrUtil.isBlank;
import static cn.hutool.core.util.StrUtil.isNotBlank;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.data.validation.support.javax.ValidationUtil.validateToThrow;
import static kunlun.exception.util.VerifyUtils.isFalse;
import static kunlun.exception.util.VerifyUtils.isTrue;
import static kunlun.exception.util.VerifyUtils.notNull;

/**
 * 内容用户端（门户）的服务实现类：仅已发布且有效期内的内容，登录即可访问；
 * 已读/未读交互落 cms_article_read（一人一文一条）.
 * <p>
 * 可见性口径：status=1 AND publish_time&lt;=now AND (expire_time IS NULL OR expire_time&gt;now)；
 * categoryId=0 哨兵表示未分类（category_id 为空）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Slf4j
@Service
public class ArticlePortalServiceImpl implements ArticlePortalService {

    /**
     * 未分类哨兵值（cms_category.id 自增恒非 0，哨兵安全）
     */
    private static final long UNCATEGORIZED = 0L;

    /**
     * 首页最新公告默认条数
     */
    private static final int NOTICE_LATEST_DEFAULT = 5;

    /**
     * 全文搜索命中片段：命中关键字前后各保留的字符数
     */
    private static final int SNIPPET_RADIUS = 60;

    @Resource
    private ArticleService articleService;
    @Resource
    private ArticleReadMapper articleReadMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public Page<PortalResult> queryPage(PortalQuery query) {
        // 参数校验（当前用户须在 startPage 前解析，避免安全上下文惰性加载触发 SQL 吃掉分页上下文）
        validateToThrow(query);
        Long userId = currentUser();
        // 查询（仅已发布且有效期内，置顶权重倒序 → 发布时间倒序）
        Date now = new Date();
        PageUtil.startPage(query.getPageNum(), query.getPageSize());
        List<Article> list = articleService.list(buildVisibleWrapper(query, now)
                .orderByDesc(Article::getTopWeight)
                .orderByDesc(Article::getPublishTime));
        // 转换并填充（分页元信息取自原 pagehelper 列表，行数据用组装后的副本）
        List<PortalResult> rows = convertRows(list);
        Page<PortalResult> result = PageUtil.handleResult(list, PortalResult.class);
        result.setData(rows);
        fillPortalRows(result.getData(), userId);
        fillSnippets(result.getData(), query);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PortalResult detail(Long recordId) {
        // 参数校验
        notNull(recordId, "文章ID不能为空！");
        // 查询并校验用户端可见性（已发布 + 发布时间已到 + 未过有效期）
        Article article = articleService.getById(recordId);
        notNull(article, "文章不存在或已下线！");
        Date now = new Date();
        isFalse(!Objects.equals(CmsArticleStatus.PUBLISHED.getCode(), article.getStatus()), "文章已下线！");
        isFalse(isNull(article.getPublishTime()) || article.getPublishTime().after(now), "文章尚未发布！");
        isFalse(nonNull(article.getExpireTime()) && !article.getExpireTime().after(now), "文章已过有效期！");
        // 浏览次数 +1（原子 setSql，每次打开累计，衡量热度）
        articleService.update(Wrappers.lambdaUpdate(Article.class)
                .eq(Article::getId, recordId)
                .setSql("view_count = view_count + 1"));
        // 记已读（幂等：一人一文一条，不存在才 insert；唯一键兜底并发）
        Long userId = currentUser();
        markRead(userId, recordId, now);
        // 组装结果（全字段 + 封面图/附件反序列化 + 分类名/发布人姓名回填）
        PortalResult result = BeanUtil.beanToBean(article, PortalResult.class);
        result.setCoverImageList(filesFromJson(article.getCoverImages()));
        result.setAttachmentList(filesFromJson(article.getAttachments()));
        result.setViewCount((article.getViewCount() == null ? ZERO : article.getViewCount()) + ONE);
        result.setIsRead(ONE);
        Map<Long, Category> categoryMap = refCategoryMap(singletonList(article.getCategoryId()));
        Category category = isNull(article.getCategoryId()) ? null : categoryMap.get(article.getCategoryId());
        result.setCategoryName(nonNull(category) ? category.getName() : null);
        Map<Long, SysUser> userMap = refUserMap(singletonList(article.getPublishUser()));
        SysUser user = isNull(article.getPublishUser()) ? null : userMap.get(article.getPublishUser());
        result.setPublishUserName(nonNull(user) ? user.getName() : null);
        return result;
    }

    @Override
    public List<PortalResult> noticeLatest(Integer limit) {
        // 最新已发布有效公告（内容类型=公告），按发布时间倒序前 limit 条
        Date now = new Date();
        int limitCount = limit == null || limit <= 0 ? NOTICE_LATEST_DEFAULT : limit;
        List<Article> list = articleService.list(buildVisibleWrapper(null, now)
                .eq(Article::getType, CmsArticleType.NOTICE.getCode())
                .orderByDesc(Article::getPublishTime)
                .last(String.format("LIMIT %d", limitCount)));
        // 转换并填充
        List<PortalResult> rows = convertRows(list);
        fillPortalRows(rows, currentUser());
        return rows;
    }

    @Override
    public Long unreadCount() {
        // 未读数（公告口径：内容类型=12）= 有效期内已发布内容数 − 本人已读（notExists 过滤已读）
        Long userId = currentUser();
        Date now = new Date();
        return articleService.count(buildVisibleWrapper(null, now)
                .eq(Article::getType, CmsArticleType.NOTICE.getCode())
                .notExists(buildNotReadExistsSql(userId)));
    }

    @Override
    public CategoryPortalListResult categoryList(Integer type) {
        // 参数校验：内容类型须为已注册的非缺省码值（0 缺省分类挂不到任何内容，不进文件夹区）
        isTrue(nonNull(type) && CmsArticleType.contains(type)
                && !Objects.equals(CmsArticleType.DEFAULT.getCode(), type), "非法的内容类型！");
        Date now = new Date();
        // 启用分类（类型精确匹配，sort 升序、id 兜底稳定次序）
        List<Category> categories = categoryMapper.selectList(Wrappers.lambdaQuery(Category.class)
                .eq(Category::getType, type)
                .eq(Category::getEnabled, ONE)
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId));
        // 各分类可见内容计数（一条 GROUP BY：含分类被禁用/删除的悬挂桶，"全部"总数取各组之和才准确）
        List<Map<String, Object>> countRows = articleService.selectJoinMaps(buildVisibleWrapper(null, now)
                .eq(Article::getType, type)
                .selectAs(Article::getCategoryId, "categoryId")
                .selectCount(Article::getId, "articleCount")
                .groupBy(Article::getCategoryId));
        long totalCount = 0L;
        long uncategorizedCount = 0L;
        Map<Long, Long> countMap = new HashMap<>();
        // ⚠️ 计数行遍历不得在 for-each 内联三元+diamond（JDK8 泛型推断会塌成 Object 元素，编译失败）
        if (isNotEmpty(countRows)) {
            for (Map<String, Object> row : countRows) {
                long count = ((Number) row.get("articleCount")).longValue();
                totalCount += count;
                Object categoryId = row.get("categoryId");
                if (isNull(categoryId)) {
                    uncategorizedCount = count;
                }
                else {
                    countMap.put(((Number) categoryId).longValue(), count);
                }
            }
        }
        // 组装（beanToBean 按名拷贝基础字段，计数由分组结果回填；无内容分类计 0）
        CategoryPortalListResult result = new CategoryPortalListResult();
        result.setTotalCount(totalCount);
        result.setUncategorizedCount(uncategorizedCount);
        List<CategoryPortalResult> categoryList = new ArrayList<>(categories.size());
        for (Category category : categories) {
            CategoryPortalResult row = BeanUtil.beanToBean(category, CategoryPortalResult.class);
            row.setArticleCount(countMap.getOrDefault(category.getId(), 0L));
            categoryList.add(row);
        }
        result.setCategoryList(categoryList);
        return result;
    }

    @Override
    public Page<PortalResult> myRead(PortalQuery query) {
        // 参数校验（当前用户须在 startPage 前解析：安全上下文惰性加载会触发 SQL 吃掉分页上下文）
        validateToThrow(query);
        Long userId = currentUser();
        // 我的已读 = 已读表关联内容，按首次阅读时间倒序；过滤条件挂到内容列
        PageUtil.startPage(query.getPageNum(), query.getPageSize());
        MPJLambdaWrapper<ArticleRead> wrapper = JoinWrappers.lambda(ArticleRead.class)
                .selectAs(ArticleRead::getArticleId, PortalResult::getId)
                .selectAs(ArticleRead::getReadTime, PortalResult::getReadTime)
                .leftJoin(Article.class, Article::getId, ArticleRead::getArticleId)
                .eq(ArticleRead::getUserId, userId)
                .orderByDesc(ArticleRead::getReadTime);
        applyPortalFilters(wrapper, query);
        // 只取 articleId + readTime（避免内容大字段直接映射到出参），再按 id 批量组装全字段
        List<PortalResult> rows = articleReadMapper.selectJoinList(PortalResult.class, wrapper);
        List<Long> articleIds = rows.stream().filter(Objects::nonNull)
                .map(PortalResult::getId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, Article> entityMap = CollUtil.isEmpty(articleIds) ? emptyMap() : articleService.mapByIds(articleIds);
        List<PortalResult> data = new ArrayList<>(rows.size());
        for (PortalResult row : rows) {
            if (row == null || isNull(row.getId())) { continue; }
            Article article = entityMap.get(row.getId());
            if (isNull(article)) { continue; }
            PortalResult item = BeanUtil.beanToBean(article, PortalResult.class);
            item.setCoverImageList(filesFromJson(article.getCoverImages()));
            item.setAttachmentList(filesFromJson(article.getAttachments()));
            item.setReadTime(row.getReadTime());
            item.setIsRead(ONE);
            data.add(item);
        }
        Page<PortalResult> result = PageUtil.handleResult(rows);
        result.setData(data);
        fillNames(data);
        return result;
    }

    @Override
    public Page<PortalResult> myUnread(PortalQuery query) {
        // 参数校验
        validateToThrow(query);
        Long userId = currentUser();
        // 我的未读 = 用户端可见列表过滤未读（notExists 已读表）
        Date now = new Date();
        PageUtil.startPage(query.getPageNum(), query.getPageSize());
        List<Article> list = articleService.list(buildVisibleWrapper(query, now)
                .notExists(buildNotReadExistsSql(userId))
                .orderByDesc(Article::getTopWeight)
                .orderByDesc(Article::getPublishTime));
        // 转换并填充（未读恒 0；分页元信息取自原 pagehelper 列表，行数据用组装后的副本）
        List<PortalResult> rows = convertRows(list);
        Page<PortalResult> result = PageUtil.handleResult(list, PortalResult.class);
        result.setData(rows);
        fillNames(result.getData());
        result.getData().forEach(row -> row.setIsRead(ZERO));
        return result;
    }

    /**
     * 构建用户端可见性查询：已发布 + 发布时间已到 +（无下线时间或未到期）；
     * 并应用门户过滤条件（categoryId 哨兵/typeList/keyword）.
     */
    private MPJLambdaWrapper<Article> buildVisibleWrapper(PortalQuery query, Date now) {
        MPJLambdaWrapper<Article> wrapper = JoinWrappers.lambda(Article.class)
                .eq(Article::getStatus, CmsArticleStatus.PUBLISHED.getCode())
                .le(Article::getPublishTime, now)
                .and(w -> w.isNull(Article::getExpireTime).or().gt(Article::getExpireTime, now));
        applyPortalFilters(wrapper, query);
        return wrapper;
    }

    /**
     * 应用门户过滤条件（categoryId 哨兵/typeList/keyword；fullText=true 时 keyword 全文匹配标题/摘要/正文）.
     * <p>
     * 泛型 T 同时兼容内容表 wrapper（queryPage/myUnread）与已读表 wrapper（myRead）：
     * MPJ 的列条件方法支持任意实体 SFunction 引用.
     */
    private <T> void applyPortalFilters(MPJLambdaWrapper<T> wrapper, PortalQuery query) {
        if (isNull(query)) { return; }
        if (nonNull(query.getCategoryId())) {
            // 分类过滤：0 = 未分类（category_id 为空），非 0 = 精确分类
            if (UNCATEGORIZED == query.getCategoryId()) {
                wrapper.isNull(Article::getCategoryId);
            }
            else {
                wrapper.eq(Article::getCategoryId, query.getCategoryId());
            }
        }
        if (isNotEmpty(query.getTypeList())) { wrapper.in(Article::getType, query.getTypeList()); }
        if (isNotBlank(query.getKeyword())) {
            String keyword = query.getKeyword();
            if (Boolean.TRUE.equals(query.getFullText())) {
                // 全文口径：标题 / 摘要 / 正文任一命中（内容量级小，正文 LIKE 可接受、不建全文索引）
                wrapper.and(w -> w.like(Article::getTitle, keyword)
                        .or().like(Article::getSummary, keyword)
                        .or().like(Article::getContent, keyword));
            }
            else {
                wrapper.like(Article::getTitle, keyword);
            }
        }
    }

    /**
     * 构建"当前用户未读"的 notExists 子查询（userId 为数值型，无注入风险）.
     * <p>
     * 关联列引用主表别名 t（MyBatis-Plus/MPJ 给主表固定起别名 t，表已别名后不能再以表名引用）；
     * 手写 SQL 不走逻辑删自动过滤，cms_article_read 须显式补 delete_status = 0.
     */
    private String buildNotReadExistsSql(Long userId) {
        return String.format("SELECT 1 FROM cms_article_read r WHERE r.delete_status = 0"
                        + " AND r.user_id = %d AND r.article_id = t.id", userId);
    }

    /**
     * 记已读（不存在才 insert；唯一键 uk_article_user 兜底并发，插入冲突忽略）.
     */
    private void markRead(Long userId, Long recordId, Date now) {
        notNull(userId, "请先登录！");
        Long exists = articleReadMapper.selectCount(Wrappers.lambdaQuery(ArticleRead.class)
                .eq(ArticleRead::getArticleId, recordId)
                .eq(ArticleRead::getUserId, userId));
        if (nonNull(exists) && exists > 0) { return; }
        ArticleRead read = new ArticleRead();
        read.setArticleId(recordId);
        read.setUserId(userId);
        read.setReadTime(now);
        try {
            articleReadMapper.insert(read);
        }
        catch (Exception e) {
            // 并发下唯一键冲突（uk_article_user）视为已存在，忽略
            log.info("记已读并发冲突（视为已存在），articleId={} userId={}", recordId, userId);
        }
    }

    /**
     * 实体列表 → 门户结果列表（封面图/附件 JSON 反序列化；分类名/发布人姓名后续批量回填）.
     */
    private List<PortalResult> convertRows(List<Article> list) {
        if (CollUtil.isEmpty(list)) { return new ArrayList<>(); }
        List<PortalResult> rows = new ArrayList<>(list.size());
        for (Article article : list) {
            if (article == null) { continue; }
            PortalResult row = BeanUtil.beanToBean(article, PortalResult.class);
            row.setCoverImageList(filesFromJson(article.getCoverImages()));
            row.setAttachmentList(filesFromJson(article.getAttachments()));
            rows.add(row);
        }
        return rows;
    }

    /**
     * 填充门户行：分类名/发布人姓名回填 + 当前用户已读态.
     */
    private void fillPortalRows(List<PortalResult> rows, Long userId) {
        if (CollUtil.isEmpty(rows)) { return; }
        // 分类名/发布人姓名
        fillNames(rows);
        // 当前用户已读态（本页 ID 批量查已读表）
        Set<Long> readIds = readArticleIds(userId, rows.stream().filter(Objects::nonNull)
                .map(PortalResult::getId).filter(Objects::nonNull).distinct().collect(Collectors.toList()));
        for (PortalResult row : rows) {
            if (row == null || isNull(row.getId())) { continue; }
            row.setIsRead(readIds.contains(row.getId()) ? ONE : ZERO);
        }
    }

    /**
     * 分类名/发布人姓名回填（本批批量查，避免 N+1）.
     */
    private void fillNames(List<PortalResult> rows) {
        if (CollUtil.isEmpty(rows)) { return; }
        List<Long> categoryIds = rows.stream().filter(Objects::nonNull)
                .map(PortalResult::getCategoryId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, Category> categoryMap = refCategoryMap(categoryIds);
        List<Long> userIds = rows.stream().filter(Objects::nonNull)
                .map(PortalResult::getPublishUser).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, SysUser> userMap = refUserMap(userIds);
        for (PortalResult row : rows) {
            if (row == null) { continue; }
            Category category = isNull(row.getCategoryId()) ? null : categoryMap.get(row.getCategoryId());
            row.setCategoryName(nonNull(category) ? category.getName() : null);
            SysUser user = isNull(row.getPublishUser()) ? null : userMap.get(row.getPublishUser());
            row.setPublishUserName(nonNull(user) ? user.getName() : null);
        }
    }

    /**
     * 当前用户在给定内容上的已读集合.
     */
    private Set<Long> readArticleIds(Long userId, List<Long> articleIds) {
        if (isNull(userId) || CollUtil.isEmpty(articleIds)) { return emptySet(); }
        List<ArticleRead> reads = articleReadMapper.selectList(Wrappers.lambdaQuery(ArticleRead.class)
                .eq(ArticleRead::getUserId, userId)
                .in(ArticleRead::getArticleId, articleIds)
                .select(ArticleRead::getArticleId));
        if (CollUtil.isEmpty(reads)) { return emptySet(); }
        return reads.stream().filter(Objects::nonNull)
                .map(ArticleRead::getArticleId).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * 全文搜索时回填命中片段：正文命中才有值，仅标题/摘要命中时为空（前端回落摘要展示）.
     */
    private void fillSnippets(List<PortalResult> rows, PortalQuery query) {
        if (CollUtil.isEmpty(rows) || !Boolean.TRUE.equals(query.getFullText()) || isBlank(query.getKeyword())) {
            return;
        }
        rows.forEach(row -> {
            if (row == null) { return; }
            row.setSnippet(CmsTextUtil.snippet(row.getContent(), query.getKeyword(), SNIPPET_RADIUS));
        });
    }

    /**
     * 批量查分类（id 集合为空/全空值返回空 Map）.
     */
    private Map<Long, Category> refCategoryMap(List<Long> categoryIds) {
        List<Long> ids = CollUtil.isEmpty(categoryIds) ? java.util.Collections.emptyList()
                : categoryIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollUtil.isEmpty(ids)) { return emptyMap(); }
        List<Category> categories = categoryMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(categories)) { return emptyMap(); }
        return categories.stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(Category::getId, Function.identity(), (k1, k2) -> k1));
    }

    /**
     * 批量查用户（id 集合为空/全空值返回空 Map）.
     */
    private Map<Long, SysUser> refUserMap(List<Long> userIds) {
        List<Long> ids = CollUtil.isEmpty(userIds) ? java.util.Collections.emptyList()
                : userIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollUtil.isEmpty(ids)) { return emptyMap(); }
        List<SysUser> users = sysUserMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(users)) { return emptyMap(); }
        return users.stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(SysUser::getId, Function.identity(), (k1, k2) -> k1));
    }

    /**
     * 封面图/附件库存 JSON → 列表（空/解析失败返回 null，展示按无处理；与 ArticleServiceImpl 同口径）.
     */
    static List<FileDTO> filesFromJson(String json) {
        return ArticleServiceImpl.filesFromJson(json);
    }

    /**
     * 当前登录用户ID（无安全上下文/无登录人时返回 null）.
     */
    static Long currentUser() {
        return ArticleServiceImpl.currentUser();
    }

}
