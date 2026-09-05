package hutian.system.pojo.param;

import kunlun.core.annotation.FieldMeta;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 菜单表的编辑操作的入参对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class MenuEditParam implements Serializable {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空")
    @FieldMeta(description = "主键ID")
    private Long id;
    /**
     * 父菜单ID（0=根）
     */
    @FieldMeta(description = "父菜单ID")
    private Long parentId;
    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    @FieldMeta(description = "菜单名称")
    private String name;
    /**
     * 菜单类型：1 目录，2 菜单，3 按钮
     */
    @NotNull(message = "菜单类型不能为空")
    @FieldMeta(description = "菜单类型")
    private Integer type;
    /**
     * 菜单描述
     */
    @FieldMeta(description = "菜单描述")
    private String description;
    /**
     * 图标
     */
    @FieldMeta(description = "图标")
    private String icon;
    /**
     * 前端路由地址
     */
    @FieldMeta(description = "前端路由地址")
    private String route;
    /**
     * 前端组件路径
     */
    @FieldMeta(description = "前端组件路径")
    private String component;
    /**
     * 权限标识（按钮类型必填，如 system:user:add）
     */
    @FieldMeta(description = "权限标识")
    private String permission;
    /**
     * 排序（越小越靠前）
     */
    @FieldMeta(description = "排序")
    private Integer sort;
    /**
     * 是否启用：0 未启用，1 启用
     */
    @NotNull(message = "是否启用不能为空")
    @FieldMeta(description = "是否启用")
    private Integer enabled;
    /**
     * 是否显示：0 隐藏，1 显示
     */
    @NotNull(message = "是否显示不能为空")
    @FieldMeta(description = "是否显示")
    private Integer showFlag;

}
