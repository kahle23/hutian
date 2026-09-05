package hutian.system.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 菜单表（菜单兼权限点）的数据库实体.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("sys_menu")
public class Menu implements Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 菜单类型：0 未知，1 目录，2 菜单，3 按钮
     */
    private Integer type;
    /**
     * 菜单描述
     */
    private String description;
    /**
     * 图标
     */
    private String icon;
    /**
     * 前端路由地址
     */
    private String route;
    /**
     * 前端组件路径
     */
    private String component;
    /**
     * 权限标识（如 system:user:add）
     */
    private String permission;
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
    /**
     * 平台
     */
    @TableField(fill = FieldFill.INSERT)
    private String platform;
    /**
     * 租户标识
     */
    @TableField(fill = FieldFill.INSERT)
    private String tenantId;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改人
     */
    private String modifyUser;
    /**
     * 修改时间
     */
    private Date modifyTime;
    /**
     * 删除状态：0 未删除，1 已删除
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteStatus;

}
