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
 * 菜单表的查询对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MenuQuery extends BaseQuery implements Serializable {

    /**
     * 主键ID
     */
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    private List<Long> idList;
    /**
     * 父菜单ID
     */
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    private List<Long> parentIdList;
    /**
     * 菜单名称（支持模糊搜索）
     */
    private String nameLike;
    /**
     * 菜单类型：1 目录，2 菜单，3 按钮
     */
    private Integer type;
    /**
     * 是否启用：0 未启用，1 启用
     */
    private Integer enabled;
    /**
     * 是否显示：0 隐藏，1 显示
     */
    private Integer showFlag;
    /**
     * 菜单描述（支持模糊搜索）
     */
    private String descriptionLike;
    /**
     * 权限标识（支持模糊搜索）
     */
    private String permissionLike;
    /**
     * 前端路由地址（支持模糊搜索）
     */
    private String routeLike;
    /**
     * 前端组件路径（支持模糊搜索）
     */
    private String componentLike;

}
