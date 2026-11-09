package hutian.cms.service.impl;

import baibao.db.jdbc.mybatisplus.base.BaseServiceImpl;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import hutian.cms.enums.CmsArticleType;
import hutian.cms.mapper.ArticleMapper;
import hutian.cms.mapper.CategoryMapper;
import hutian.cms.pojo.entity.Article;
import hutian.cms.pojo.entity.Category;
import hutian.cms.pojo.param.CategoryAddParam;
import hutian.cms.pojo.param.CategoryEditParam;
import hutian.cms.pojo.param.CategoryEnableParam;
import hutian.cms.pojo.query.CategoryQuery;
import hutian.cms.pojo.result.CategoryResult;
import hutian.cms.service.ArticleService;
import hutian.cms.service.CategoryService;
import kunlun.data.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.util.StrUtil.isBlank;
import static cn.hutool.core.util.StrUtil.isNotBlank;
import static java.util.Objects.nonNull;
import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.exception.util.VerifyUtils.isFalse;
import static kunlun.exception.util.VerifyUtils.notNull;

/**
 * 内容分类表的服务实现类（平铺，无父子树）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Slf4j
@Service
public class CategoryServiceImpl extends BaseServiceImpl<CategoryMapper, Category
        , CategoryAddParam, CategoryEditParam, CategoryQuery, CategoryResult> implements CategoryService {

    @Resource
    private ArticleService articleService;
    @Resource
    private ArticleMapper articleMapper;

    @Override
    protected Category fromAddParam(CategoryAddParam param) {
        Category entity = BeanUtil.beanToBean(param, Category.class);
        // 内容类型校验归一（type 必填由入参校验保证）
        if (nonNull(param.getType())) { checkType(param.getType()); }
        // 默认值
        entity.setEnabled(nonNull(param.getEnabled()) ? param.getEnabled() : ONE);
        entity.setSort(nonNull(param.getSort()) ? param.getSort() : ZERO);
        return entity;
    }

    @Override
    protected Category fromEditParam(CategoryResult old, CategoryEditParam param) {
        Category entity = BeanUtil.beanToBean(param, Category.class);
        // 内容类型：传了才校验值域（不传沿用原值，updateById 跳过 null 字段）
        if (nonNull(param.getType())) { checkType(param.getType()); }
        return entity;
    }

    @Override
    public Long addRecord(CategoryAddParam param) {
        // 编码唯一校验（编码选填，为空不校验；仅未删除范围，软删行不占位）
        if (isNotBlank(param.getCode())) {
            isFalse(existByCode(param.getCode(), null), "该分类编码已存在！");
        }
        return super.addRecord(param);
    }

    @Override
    public void editRecord(CategoryEditParam param) {
        // 数据存在性校验
        notNull(getById(param.getId()), "分类不存在或已被删除！");
        // 编码唯一校验（排除自身）
        if (isNotBlank(param.getCode())) {
            isFalse(existByCode(param.getCode(), param.getId()), "该分类编码已存在！");
        }
        // 需要置空字段（如清空编码）时，前端在 param.clearFields 传入属性名集合，由 BaseServiceImpl 统一置空
        super.editRecord(param);
    }

    @Override
    public void enable(CategoryEnableParam param) {
        // 参数校验
        notNull(param, "启停数据不能为空！");
        isFalse(CollUtil.isEmpty(param.getIds()), "请选择要启停的分类！");
        notNull(param.getEnabled(), "启用/禁用状态值不能为空！");
        // 批量更新启用状态（0 禁用，1 启用）
        updateStatusBy(Category::getId, param.getIds(), Category::getEnabled, null, param.getEnabled());
    }

    /**
     * 分类下存在未删除内容时禁止删除（报错提示）.
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByLongIds(Collection<Long> ids) {
        // 参数校验
        if (CollUtil.isEmpty(ids)) { return; }
        // 查询数据
        List<Category> categoryList = listByIds(ids);
        for (Category category : categoryList) {
            // 分类下存在未删除内容时禁止删除
            isFalse(articleService.existBy(Article::getCategoryId, category.getId())
                    , String.format("分类「%s」下仍有内容，请先转移或删除内容！", category.getName()));
        }
        super.deleteByLongIds(ids);
    }

    @Override
    public boolean existByCode(String code, Long id) {
        // 参数校验
        isFalse(isBlank(code), "分类编码不能为空！");
        // 查询（全局逻辑删除自动过滤 delete_status=0，软删行不占位）
        return count(Wrappers.lambdaQuery(Category.class)
                .eq(Category::getCode, code)
                .ne(nonNull(id), Category::getId, id)
        ) > 0;
    }

    @Override
    protected MPJLambdaWrapper<Category> buildQueryWrapper(CategoryQuery query) {
        return JoinWrappers.lambda(Category.class)
                .in(isNotEmpty(query.getIdList()), Category::getId, query.getIdList())
                .like(isNotBlank(query.getNameLike()), Category::getName, query.getNameLike())
                .eq(isNotBlank(query.getCode()), Category::getCode, query.getCode())
                .in(isNotEmpty(query.getTypeList()), Category::getType, query.getTypeList())
                .eq(nonNull(query.getEnabled()), Category::getEnabled, query.getEnabled())
                // 默认排序：排序值升序 → id 倒序
                // （前端传了自定义排序 query.getSortFields() 时由 BaseServiceImpl 统一应用，此处条件守卫避免抢占）
                .orderByAsc(CollUtil.isEmpty(query.getSortFields()), Category::getSort)
                .orderByDesc(CollUtil.isEmpty(query.getSortFields()), Category::getId)
        ;
    }

    /**
     * 统一的内容分类表数据的处理逻辑：回填 articleCount（该分类未删文章数，批量计数）.
     */
    @Override
    protected void processData(CategoryQuery qry, List<CategoryResult> data) {
        if (CollUtil.isEmpty(data)) { return; }
        // 数据提取（仅本页出现的分类）
        List<Long> idList = data.stream().filter(Objects::nonNull)
                .map(CategoryResult::getId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, Long> countMap = new HashMap<>();
        if (isNotEmpty(idList)) {
            // 一条 GROUP BY 批量计数（主表逻辑删除自动过滤）
            MPJLambdaWrapper<Article> wrapper = JoinWrappers.lambda(Article.class)
                    .selectAs(Article::getCategoryId, "categoryId")
                    .selectCount(Article::getId, "articleCount")
                    .in(Article::getCategoryId, idList)
                    .groupBy(Article::getCategoryId);
            List<Map<String, Object>> countRows = articleMapper.selectJoinMaps(wrapper);
            if (isNotEmpty(countRows)) {
                for (Map<String, Object> row : countRows) {
                    Object categoryId = row.get("categoryId");
                    Object articleCount = row.get("articleCount");
                    if (nonNull(categoryId) && nonNull(articleCount)) {
                        countMap.put(((Number) categoryId).longValue(), ((Number) articleCount).longValue());
                    }
                }
            }
        }
        // 计数回填（无文章的分类计 0）
        for (CategoryResult item : data) {
            if (item == null) { continue; }
            item.setArticleCount(countMap.getOrDefault(item.getId(), 0L));
        }
    }

    /**
     * 内容类型校验：须为 CmsArticleType 已注册值（0 缺省分类挂不到文章，接口不落库）.
     */
    private void checkType(Integer type) {
        isFalse(!(CmsArticleType.contains(type) && !Objects.equals(CmsArticleType.DEFAULT.getCode(), type))
                , "非法的内容类型！");
    }

    /**
     * 分类变动日志暂不接入（待系统域统一接入变动日志体系）.
     */
    @Override
    protected void changeLog(Object bizId, Object oldData, Object newData, Class<?> targetClz, Object... arguments) {
    }

}
