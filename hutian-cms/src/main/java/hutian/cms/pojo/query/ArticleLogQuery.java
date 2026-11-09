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
 * 内容生命周期留痕表的查询对象.
 * <p>
 * articleId 由分页查询端点在控制器层校验必填（getById2 详情链路仅按 idList 查询，不携带 articleId）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArticleLogQuery extends BaseQuery implements Serializable {

    /**
     * 主键ID集合（getById2 详情链路自动使用）
     */
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    private List<Long> idList;
    /**
     * 文章id（分页查询必填，控制器层校验）
     */
    private Long articleId;
    /**
     * 动作类型集合：1 创建，2 发布，3 下线，4 置顶，5 取消置顶，6 编辑，7 复制
     */
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    private List<Integer> actionTypeList;

}
