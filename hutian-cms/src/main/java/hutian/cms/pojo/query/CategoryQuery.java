package hutian.cms.pojo.query;

import baibao.common.dto.base.BaseQuery;
import kunlun.data.json.support.jackson.annotation.JsonSceneDeserialize;
import kunlun.data.json.support.jackson.model.Scene;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 内容分类表的查询对象.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CategoryQuery extends BaseQuery implements Serializable {

    /**
     * 主键ID集合
     */
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    private List<Long> idList;
    /**
     * 分类名称（支持模糊搜索）
     */
    private String nameLike;
    /**
     * 分类编码（精确匹配）
     */
    private String code;
    /**
     * 内容类型集合：0 缺省，1 文章，11 版本，12 公告，13 帮助，14 提示
     */
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    private List<Integer> typeList;
    /**
     * 是否启用：0 禁用，1 启用
     */
    private Integer enabled;

}
