package hutian.cms.service.impl;

import baibao.common.dto.FileDTO;
import baibao.common.enums.QueryMode;
import baibao.db.jdbc.mybatisplus.base.BaseServiceImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import hutian.cms.enums.CmsArticleContentType;
import hutian.cms.enums.CmsArticleLogAction;
import hutian.cms.enums.CmsArticleStatus;
import hutian.cms.enums.CmsArticleType;
import hutian.cms.mapper.ArticleMapper;
import hutian.cms.mapper.ArticleReadMapper;
import hutian.cms.mapper.CategoryMapper;
import hutian.cms.mapper.SysUserMapper;
import hutian.cms.pojo.entity.Article;
import hutian.cms.pojo.entity.ArticleLog;
import hutian.cms.pojo.entity.ArticleRead;
import hutian.cms.pojo.entity.Category;
import hutian.cms.pojo.entity.SysUser;
import hutian.cms.pojo.param.ArticleAddParam;
import hutian.cms.pojo.param.ArticleEditParam;
import hutian.cms.pojo.param.ArticlePublishParam;
import hutian.cms.pojo.param.ArticleTopParam;
import hutian.cms.pojo.query.ArticleQuery;
import hutian.cms.pojo.query.ArticleReadQuery;
import hutian.cms.pojo.result.ArticleLogResult;
import hutian.cms.pojo.result.ArticleResult;
import hutian.cms.pojo.result.ReadStatResult;
import hutian.cms.pojo.result.ReadUserResult;
import hutian.cms.service.ArticleLogService;
import hutian.cms.service.ArticleService;
import hutian.cms.util.CmsTextUtil;
import kunlun.common.Page;
import kunlun.data.bean.BeanUtil;
import kunlun.security.SecurityUtil;
import kunlun.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.text.CharSequenceUtil.EMPTY;
import static cn.hutool.core.util.StrUtil.isBlank;
import static cn.hutool.core.util.StrUtil.isNotBlank;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.exception.util.VerifyUtils.isFalse;
import static kunlun.exception.util.VerifyUtils.notNull;

/**
 * 内容表的服务实现类（文章/公告/版本/帮助/提示共用）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Slf4j
@Service
public class ArticleServiceImpl extends BaseServiceImpl<ArticleMapper, Article
        , ArticleAddParam, ArticleEditParam, ArticleQuery, ArticleResult> implements ArticleService {

    /**
     * 摘要自动截取长度
     */
    private static final int SUMMARY_MAX_LENGTH = 200;

    @Resource
    private ArticleLogService articleLogService;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private ArticleReadMapper articleReadMapper;

    @Override
    protected Article fromAddParam(ArticleAddParam param) {
        Article entity = BeanUtil.beanToBean(param, Article.class);
        // 默认值与归一：类型空→1 文章，正文格式非法→回落 2 Markdown，置顶权重/浏览数兜底
        entity.setType(resolveType(param.getType(), CmsArticleType.ARTICLE.getCode()));
        entity.setContentType(normalizeContentType(param.getContentType()));
        entity.setTopWeight(nonNull(param.getTopWeight()) ? param.getTopWeight() : ZERO);
        entity.setViewCount(ZERO);
        // 封面图/附件：{name,addr} 列表转 JSON 字符串入库
        entity.setCoverImages(filesToJson(param.getCoverImages()));
        entity.setAttachments(filesToJson(param.getAttachments()));
        // 摘要为空时按正文提纯截前 200 字
        entity.setSummary(isBlank(param.getSummary()) ? buildSummary(param.getContent()) : param.getSummary());
        // 状态判定：status=1 直接发布；publishTime 为未来时间=定时发布（强制保持草稿，走 /publish 登记语义）
        // ⚠️ now 截断到整秒：DATETIME(0) 列对毫秒值四舍五入，直接写 new Date() 会使 publish_time 落入未来
        // 1 秒（32.9xx→33），门户可见性（publish_time<=now）在当前秒内误判「文章尚未发布！」
        Date now = DateUtil.beginOfSecond(new Date());
        boolean scheduled = nonNull(param.getPublishTime()) && param.getPublishTime().after(now);
        boolean publishNow = Objects.equals(ONE, param.getStatus()) && !scheduled;
        if (publishNow) {
            entity.setStatus(CmsArticleStatus.PUBLISHED.getCode());
            entity.setPublishTime(nonNull(param.getPublishTime()) ? param.getPublishTime() : now);
            entity.setPublishUser(currentUser());
        }
        else if (scheduled) {
            // 定时发布登记：保持草稿，仅登记到点时间，由定时任务到点翻转
            entity.setStatus(CmsArticleStatus.DRAFT.getCode());
            entity.setPublishTime(param.getPublishTime());
            entity.setPublishUser(null);
        }
        else {
            // 普通草稿：清掉误传的发布时间（防止 publishDue 误判为到点草稿）
            entity.setStatus(CmsArticleStatus.DRAFT.getCode());
            entity.setPublishTime(null);
            entity.setPublishUser(null);
        }
        return entity;
    }

    @Override
    protected Article fromEditParam(ArticleResult old, ArticleEditParam param) {
        Article entity = BeanUtil.beanToBean(param, Article.class);
        // 封面图/附件：传了才转 JSON 入库（不传保持 null，updateById 跳过不覆盖原值；清空走 clearFields）
        entity.setCoverImages(filesToJson(param.getCoverImages()));
        entity.setAttachments(filesToJson(param.getAttachments()));
        // 正文格式：不传沿用原值（防置空覆盖），非法值回落 2 Markdown
        entity.setContentType(isNull(param.getContentType())
                ? old.getContentType() : normalizeContentType(param.getContentType()));
        // 内容类型：传了才校验值域（不传沿用原值）
        if (nonNull(param.getType())) { resolveType(param.getType(), null); }
        // 摘要：空且有新正文时按正文提纯截前 200 字（正文也没传时不动摘要，updateById 跳过）
        if (isBlank(param.getSummary()) && isNotBlank(param.getContent())) {
            entity.setSummary(buildSummary(param.getContent()));
        }
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addRecord(ArticleAddParam param) {
        // 校验分类存在且启用（分类非必填：为空表示未分类）
        checkCategoryUsable(param.getCategoryId());
        // 新增
        Long recordId = super.addRecord(param);
        // 生命周期留痕与发布联动
        Article article = getById(recordId);
        articleLogService.writeLog(recordId, CmsArticleLogAction.CREATE, null);
        if (nonNull(article) && Objects.equals(CmsArticleStatus.PUBLISHED.getCode(), article.getStatus())) {
            // 创建即发布：补发布留痕（与定时发布到点区分）
            articleLogService.writeLog(recordId, CmsArticleLogAction.PUBLISH, "手动发布");
        }
        return recordId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editRecord(ArticleEditParam param) {
        // 数据存在性校验（草稿/已发布/已下线均可编辑，已发布内容修改即时生效）
        notNull(getById(param.getId()), "内容不存在或已删除！");
        // 校验分类存在且启用
        checkCategoryUsable(param.getCategoryId());
        // 编辑（status/publishTime 不在此接口改，走 publish/offline 专门接口）
        super.editRecord(param);
        // 生命周期留痕
        articleLogService.writeLog(param.getId(), CmsArticleLogAction.EDIT, "编辑内容");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(ArticlePublishParam param) {
        // 定时发布判定：传了未来时间则保持草稿，由定时任务到点发布
        // ⚠️ now 截断到整秒（与 fromAddParam 同口径）：防 DATETIME(0) 毫秒进位使 publish_time 落入未来 1 秒
        Date now = DateUtil.beginOfSecond(new Date());
        boolean scheduled = nonNull(param.getPublishTime()) && param.getPublishTime().after(now);
        // 查询数据
        List<Article> articleList = listByIds(param.getIds());
        isFalse(CollUtil.isEmpty(articleList), "待发布的内容不存在！");
        // 状态校验、赋值与留痕（republish 须在状态覆盖前判定，可区分首次发布/重新发布）
        Map<Long, String> logDetails = new LinkedHashMap<>();
        for (Article article : articleList) {
            // 前置状态校验：发布支持草稿(0)，重新发布支持已下线(2)，已发布(1)拦截
            boolean republish = Objects.equals(CmsArticleStatus.OFFLINE.getCode(), article.getStatus());
            isFalse(!(republish || Objects.equals(CmsArticleStatus.DRAFT.getCode(), article.getStatus()))
                    , String.format("「%s」当前状态不允许发布！", article.getTitle()));
            if (scheduled) {
                // 定时发布：保持草稿，仅登记到点时间
                article.setPublishTime(param.getPublishTime());
                logDetails.put(article.getId(), "定时发布：" + DateUtil.formatDateTime(param.getPublishTime()));
            }
            else {
                // 立即发布/重新发布：重写发布时间与发布人
                article.setStatus(CmsArticleStatus.PUBLISHED.getCode());
                article.setPublishTime(now);
                article.setPublishUser(currentUser());
                logDetails.put(article.getId(), republish ? "重新发布" : "发布");
            }
            if (nonNull(param.getExpireTime())) {
                article.setExpireTime(param.getExpireTime());
            }
        }
        // 更新数据
        updateBatchById(articleList);
        // 逐条留痕
        for (Map.Entry<Long, String> entry : logDetails.entrySet()) {
            articleLogService.writeLog(entry.getKey(), CmsArticleLogAction.PUBLISH, entry.getValue());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offline(List<Long> ids) {
        // 参数校验
        isFalse(CollUtil.isEmpty(ids), "请选择要下线的内容！");
        // 查询数据
        List<Article> articleList = listByIds(ids);
        for (Article article : articleList) {
            // 前置状态校验：仅已发布可下线
            isFalse(!Objects.equals(CmsArticleStatus.PUBLISHED.getCode(), article.getStatus())
                    , String.format("「%s」未处于发布状态，无法下线！", article.getTitle()));
            article.setStatus(CmsArticleStatus.OFFLINE.getCode());
        }
        // 更新数据
        updateBatchById(articleList);
        // 留痕
        for (Article article : articleList) {
            articleLogService.writeLog(article.getId(), CmsArticleLogAction.OFFLINE, "下线");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void top(ArticleTopParam param) {
        // 数据存在性校验
        Article article = getById(param.getId());
        notNull(article, "内容不存在或已删除！");
        // 置顶权重：>0 置顶且值越大越靠前，0 取消置顶，负数拦截
        int topWeight = nonNull(param.getTopWeight()) ? param.getTopWeight() : ZERO;
        isFalse(topWeight < ZERO, "置顶权重不允许为负数！");
        article.setTopWeight(topWeight);
        updateById(article);
        // 生命周期留痕
        boolean topFlag = topWeight > ZERO;
        articleLogService.writeLog(param.getId(), topFlag ? CmsArticleLogAction.TOP : CmsArticleLogAction.CANCEL_TOP
                , topFlag ? "置顶权重：" + topWeight : null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copy(Long recordId) {
        // 数据存在性校验
        Article old = getById(recordId);
        notNull(old, "内容不存在或已删除！");
        // 复制为草稿：清空置顶/发布/到期/浏览数，发布信息走重新登记
        Article entity = BeanUtil.beanToBean(old, Article.class);
        entity.setId(null);
        entity.setTitle(old.getTitle() + "（副本）");
        entity.setStatus(CmsArticleStatus.DRAFT.getCode());
        entity.setTopWeight(ZERO);
        entity.setPublishTime(null);
        entity.setExpireTime(null);
        entity.setPublishUser(null);
        entity.setViewCount(ZERO);
        // 清空审计字段，由框架重新填充
        entity.setPlatform(null);
        entity.setTenantId(null);
        entity.setCreateUser(null);
        entity.setCreateTime(null);
        entity.setModifyUser(null);
        entity.setModifyTime(null);
        entity.setDeleteStatus(null);
        save(entity);
        // 生命周期留痕
        articleLogService.writeLog(entity.getId(), CmsArticleLogAction.COPY, "复制来源：" + old.getId());
        articleLogService.writeLog(entity.getId(), CmsArticleLogAction.CREATE, "存为草稿");
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int publishDue() {
        // 扫描到点草稿（publish_time 为 NULL 的行不会被 <= 命中，天然排除）
        ArticleQuery query = new ArticleQuery();
        query.setStatusList(singletonList(CmsArticleStatus.DRAFT.getCode()));
        query.setPublishTimeEnd(new Date());
        query.setQueryMode(QueryMode.SIMPLE);
        List<ArticleResult> dueList = queryList(query);
        if (CollUtil.isEmpty(dueList)) { return ZERO; }
        // 批量状态翻转：草稿 → 已发布（定时任务上下文无登录人，publishUser 保持为空）
        List<Long> ids = dueList.stream().filter(Objects::nonNull)
                .map(ArticleResult::getId).filter(Objects::nonNull).collect(Collectors.toList());
        updateStatusBy(Article::getId, ids, Article::getStatus, CmsArticleStatus.DRAFT.getCode()
                , CmsArticleStatus.PUBLISHED.getCode());
        // 留痕
        for (Long id : ids) {
            articleLogService.writeLog(id, CmsArticleLogAction.PUBLISH, "定时发布到点");
        }
        return ids.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int expireDue() {
        // 扫描有效期已过的已发布内容（expire_time 为 NULL = 长期有效，不会被 <= 命中）
        ArticleQuery query = new ArticleQuery();
        query.setStatusList(singletonList(CmsArticleStatus.PUBLISHED.getCode()));
        query.setExpireTimeEnd(new Date());
        query.setQueryMode(QueryMode.SIMPLE);
        List<ArticleResult> dueList = queryList(query);
        if (CollUtil.isEmpty(dueList)) { return ZERO; }
        // 下线（复用下线逻辑，含留痕）
        List<Long> ids = dueList.stream().filter(Objects::nonNull)
                .map(ArticleResult::getId).filter(Objects::nonNull).collect(Collectors.toList());
        offline(ids);
        return ids.size();
    }

    @Override
    public ReadStatResult readStat(Long recordId) {
        // 参数校验
        notNull(recordId, "内容ID不能为空！");
        // 目标人数：启用状态的用户数（逻辑删除自动过滤）
        Long targetCount = sysUserMapper.selectCount(Wrappers.lambdaQuery(SysUser.class)
                .eq(SysUser::getEnabled, ONE));
        // 已读人数：该内容的已读行数（唯一键保证一人一条）
        Long readCount = articleReadMapper.selectCount(Wrappers.lambdaQuery(ArticleRead.class)
                .eq(ArticleRead::getArticleId, recordId));
        // 组装统计结果（触达率保留 1 位小数）
        ReadStatResult stat = new ReadStatResult();
        int target = targetCount == null ? ZERO : targetCount.intValue();
        int read = readCount == null ? ZERO : readCount.intValue();
        stat.setTargetCount(target);
        stat.setReadCount(read);
        stat.setUnreadCount(Math.max(target - read, ZERO));
        stat.setReachRate(target > 0 ? Math.round(read * 1000.0 / target) / 10.0 : 0D);
        return stat;
    }

    @Override
    public Page<ReadUserResult> readUsers(Long recordId, ArticleReadQuery query) {
        // 参数校验
        notNull(recordId, "内容ID不能为空！");
        // 分页上下文须先于查询建立
        PageUtil.startPage(query.getPageNum(), query.getPageSize());
        // 已读名单：已读表关联用户回显姓名（读扩散口径，isRead 恒 1）
        MPJLambdaWrapper<ArticleRead> wrapper = JoinWrappers.lambda(ArticleRead.class)
                .selectAs(ArticleRead::getUserId, ReadUserResult::getUserId)
                .selectAs(ArticleRead::getReadTime, ReadUserResult::getReadTime)
                .leftJoin(SysUser.class, SysUser::getId, ArticleRead::getUserId)
                .selectAs(SysUser::getName, ReadUserResult::getUserName)
                .eq(ArticleRead::getArticleId, recordId)
                .orderByDesc(ArticleRead::getReadTime);
        List<ReadUserResult> list = articleReadMapper.selectJoinList(ReadUserResult.class, wrapper);
        Page<ReadUserResult> page = PageUtil.handleResult(list, ReadUserResult.class);
        page.getData().forEach(row -> row.setIsRead(ONE));
        return page;
    }

    @Override
    public Page<ReadUserResult> unreadUsers(Long recordId, ArticleReadQuery query) {
        // 参数校验
        notNull(recordId, "内容ID不能为空！");
        // 分页上下文须先于查询建立
        PageUtil.startPage(query.getPageNum(), query.getPageSize());
        // 未读名单：启用用户 LEFT JOIN 已读表，IS NULL 过滤出无已读行的用户
        MPJLambdaWrapper<SysUser> wrapper = JoinWrappers.lambda(SysUser.class)
                .selectAs(SysUser::getId, ReadUserResult::getUserId)
                .selectAs(SysUser::getName, ReadUserResult::getUserName)
                .select(ArticleRead::getId)
                .leftJoin(ArticleRead.class, on -> on
                        .eq(ArticleRead::getUserId, SysUser::getId)
                        .eq(ArticleRead::getArticleId, recordId))
                .isNull(ArticleRead::getId)
                .eq(SysUser::getEnabled, ONE)
                .orderByAsc(SysUser::getId);
        List<ReadUserResult> list = sysUserMapper.selectJoinList(ReadUserResult.class, wrapper);
        Page<ReadUserResult> page = PageUtil.handleResult(list, ReadUserResult.class);
        page.getData().forEach(row -> row.setIsRead(ZERO));
        return page;
    }

    @Override
    public ArticleResult getById2(Serializable id) {
        // 基础详情（processData 已回填分类名/发布人姓名/封面图/附件）
        ArticleResult result = super.getById2(id);
        if (isNull(result)) { return result; }
        // 生命周期留痕（按动作时间倒序，operatorName 回填）
        List<ArticleLog> logs = articleLogService.findBy(ArticleLog::getArticleId, singletonList(result.getId()));
        List<ArticleLogResult> logResults = BeanUtil.beanToBeanInList(logs, ArticleLogResult.class);
        logResults.sort(Comparator.comparing(ArticleLogResult::getActionTime
                , Comparator.nullsLast(Comparator.reverseOrder())));
        fillOperatorNames(logResults);
        result.setLogs(logResults);
        return result;
    }

    @Override
    protected MPJLambdaWrapper<Article> buildQueryWrapper(ArticleQuery query) {
        return JoinWrappers.lambda(Article.class)
                .in(isNotEmpty(query.getIdList()), Article::getId, query.getIdList())
                .in(isNotEmpty(query.getCategoryIdList()), Article::getCategoryId, query.getCategoryIdList())
                .in(isNotEmpty(query.getTypeList()), Article::getType, query.getTypeList())
                .in(isNotEmpty(query.getStatusList()), Article::getStatus, query.getStatusList())
                .like(isNotBlank(query.getTitleLike()), Article::getTitle, query.getTitleLike())
                .ge(nonNull(query.getPublishTimeBegin()), Article::getPublishTime, query.getPublishTimeBegin())
                .le(nonNull(query.getPublishTimeEnd()), Article::getPublishTime, query.getPublishTimeEnd())
                .ge(nonNull(query.getExpireTimeBegin()), Article::getExpireTime, query.getExpireTimeBegin())
                .le(nonNull(query.getExpireTimeEnd()), Article::getExpireTime, query.getExpireTimeEnd())
                // 默认排序：置顶权重倒序 → 发布时间倒序 → id 倒序
                // （前端传了自定义排序 query.getSortFields() 时由 BaseServiceImpl 统一应用，此处条件守卫避免抢占）
                .orderByDesc(CollUtil.isEmpty(query.getSortFields()), Article::getTopWeight)
                .orderByDesc(CollUtil.isEmpty(query.getSortFields()), Article::getPublishTime)
                .orderByDesc(CollUtil.isEmpty(query.getSortFields()), Article::getId)
        ;
    }

    @Override
    protected void processData(ArticleQuery qry, List<ArticleResult> data) {
        if (CollUtil.isEmpty(data)) { return; }
        // 回填封面图/附件（库存 JSON 反序列化）、分类名称与发布人姓名（本页批量查，避免 N+1）
        List<Long> idList = data.stream().filter(Objects::nonNull)
                .map(ArticleResult::getId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, Article> entityMap = CollUtil.isEmpty(idList) ? emptyMap() : mapByIds(idList);
        Map<Long, Category> categoryMap = refCategoryMap(data.stream().filter(Objects::nonNull)
                .map(ArticleResult::getCategoryId).filter(Objects::nonNull).distinct().collect(Collectors.toList()));
        Map<Long, SysUser> userMap = refUserMap(data.stream().filter(Objects::nonNull)
                .map(ArticleResult::getPublishUser).filter(Objects::nonNull).distinct().collect(Collectors.toList()));
        for (ArticleResult item : data) {
            if (item == null) { continue; }
            Article entity = entityMap.get(item.getId());
            if (nonNull(entity)) {
                item.setCoverImageList(filesFromJson(entity.getCoverImages()));
                item.setAttachmentList(filesFromJson(entity.getAttachments()));
            }
            Category category = isNull(item.getCategoryId()) ? null : categoryMap.get(item.getCategoryId());
            item.setCategoryName(nonNull(category) ? category.getName() : null);
            SysUser user = isNull(item.getPublishUser()) ? null : userMap.get(item.getPublishUser());
            item.setPublishUserName(nonNull(user) ? user.getName() : null);
        }
    }

    /**
     * 留痕操作人姓名回填（本批批量查 sys_user，定时任务产生的 NULL 操作人跳过）.
     */
    private void fillOperatorNames(List<ArticleLogResult> logResults) {
        if (CollUtil.isEmpty(logResults)) { return; }
        List<Long> operatorIds = logResults.stream().filter(Objects::nonNull)
                .map(ArticleLogResult::getOperatorId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, SysUser> userMap = refUserMap(operatorIds);
        for (ArticleLogResult row : logResults) {
            if (row == null || isNull(row.getOperatorId())) { continue; }
            SysUser user = userMap.get(row.getOperatorId());
            row.setOperatorName(nonNull(user) ? user.getName() : null);
        }
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
     * 校验分类存在且启用（分类非必填：为空表示未分类，直接放行）.
     */
    private void checkCategoryUsable(Long categoryId) {
        // 非必填：为空跳过校验（未分类）
        if (isNull(categoryId)) { return; }
        Category category = categoryMapper.selectById(categoryId);
        notNull(category, "分类不存在或已被删除！");
        isFalse(!Objects.equals(ONE, category.getEnabled())
                , String.format("分类「%s」已停用，请重新选择！", category.getName()));
    }

    /**
     * 正文格式归一：仅接受 1 纯文本 / 2 Markdown / 3 Html，其余（空/未知值/0 缺省）一律回落 2 Markdown.
     */
    private Integer normalizeContentType(Integer contentType) {
        boolean valid = CmsArticleContentType.PLAIN_TEXT.getCode().equals(contentType)
                || CmsArticleContentType.MARKDOWN.getCode().equals(contentType)
                || CmsArticleContentType.HTML.getCode().equals(contentType);
        return valid ? contentType : CmsArticleContentType.MARKDOWN.getCode();
    }

    /**
     * 内容类型校验归一：须为 CmsArticleType 已注册值且不为缺省（0 仅表结构缺省，接口不落库）；
     * 传空返回 fallback.
     */
    private Integer resolveType(Integer type, Integer fallback) {
        if (isNull(type)) { return fallback; }
        isFalse(!(CmsArticleType.contains(type) && !Objects.equals(CmsArticleType.DEFAULT.getCode(), type))
                , "非法的内容类型！");
        return type;
    }

    /**
     * 摘要为空时按正文提纯截取（HTML/Markdown 剥标记 → 折叠空白 → 截前 200 字）.
     */
    private String buildSummary(String content) {
        // 参数校验
        if (isBlank(content)) { return EMPTY; }
        String plain = CmsTextUtil.stripMarkup(content);
        plain = plain.replaceAll("\\s+", " ").trim();
        return plain.length() > SUMMARY_MAX_LENGTH ? plain.substring(0, SUMMARY_MAX_LENGTH) : plain;
    }

    /**
     * 封面图/附件列表 → JSON 字符串入库（空集合落 NULL，updateById 时跳过不覆盖）.
     */
    static String filesToJson(List<FileDTO> files) {
        if (CollUtil.isEmpty(files)) { return null; }
        return JSONUtil.toJsonStr(files);
    }

    /**
     * 库存 JSON 字符串 → 封面图/附件列表（空/解析失败返回 null，展示按无处理）.
     */
    static List<FileDTO> filesFromJson(String json) {
        if (isBlank(json)) { return null; }
        try {
            List<FileDTO> files = JSONUtil.parseArray(json).toList(FileDTO.class);
            return CollUtil.isEmpty(files) ? null : files;
        }
        catch (Exception e) {
            log.warn("解析封面图/附件 JSON 失败，按无处理：{}", json, e);
            return null;
        }
    }

    /**
     * 当前登录用户ID（无安全上下文/无登录人时返回 null）.
     */
    static Long currentUser() {
        try {
            return SecurityUtil.getUserId((Long) null);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * 文章生命周期留痕走 cms_article_log（ArticleLogService），变动日志体系暂不接入.
     */
    @Override
    protected void changeLog(Object bizId, Object oldData, Object newData, Class<?> targetClz, Object... arguments) {
    }

}
