package hutian.system.pojo.query;

import baibao.common.dto.base.BaseQuery;
import kunlun.data.json.support.jackson.annotation.JsonSceneDeserialize;
import kunlun.data.json.support.jackson.model.Scene;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 角色表的查询对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoleQuery extends BaseQuery implements Serializable {

    /**
     * 主键ID
     */
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    private List<Long> idList;
    /**
     * 角色名称（支持模糊搜索）
     */
    private String nameLike;
    /**
     * 角色编码（支持模糊搜索）
     */
    private String codeLike;
    /**
     * 描述（支持模糊搜索）
     */
    private String descriptionLike;
    /**
     * 是否启用：0 未启用，1 启用
     */
    private Integer enabled;

}
