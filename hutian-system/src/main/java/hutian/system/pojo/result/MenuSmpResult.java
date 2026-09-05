package hutian.system.pojo.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 菜单表的简单结果对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class MenuSmpResult implements Serializable {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 父菜单ID（0=根）
     */
    private Long parentId;
    /**
     * 菜单名称
     */
    private String name;
    /**
     * 菜单类型：1 目录，2 菜单，3 按钮
     */
    private Integer type;
    /**
     * 权限标识
     */
    private String permission;
    /**
     * 前端路由地址
     */
    private String route;
    /**
     * 图标
     */
    private String icon;
    /**
     * 排序（越小越靠前）
     */
    private Integer sort;
    /**
     * 是否启用：0 未启用，1 启用
     */
    private Integer enabled;
    /**
     * 是否显示：0 隐藏，1 显示
     */
    private Integer showFlag;

}
