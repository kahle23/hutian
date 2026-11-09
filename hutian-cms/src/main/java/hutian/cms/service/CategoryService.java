package hutian.cms.service;

import baibao.db.jdbc.mybatisplus.base.BaseService;
import hutian.cms.pojo.entity.Category;
import hutian.cms.pojo.param.CategoryAddParam;
import hutian.cms.pojo.param.CategoryEditParam;
import hutian.cms.pojo.param.CategoryEnableParam;
import hutian.cms.pojo.query.CategoryQuery;
import hutian.cms.pojo.result.CategoryResult;

/**
 * 内容分类表的服务类.
 *
 * @author Hutian
 * @since 2026-11-06
 */
public interface CategoryService extends BaseService<Category
        , CategoryAddParam, CategoryEditParam, CategoryQuery, CategoryResult> {

    /**
     * 批量启用/禁用分类.
     *
     * @param param 启停入参（ids + enabled）
     */
    void enable(CategoryEnableParam param);

    /**
     * 判断分类编码是否已存在（仅未删除范围；编码选填，为空不校验）.
     *
     * @param code 待校验的编码
     * @param id   需要被排除的ID（编辑时传自身ID，新增时传空）
     * @return 存在返回 true
     */
    boolean existByCode(String code, Long id);

}
