package hutian.cms.pojo.entity;

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
 * 内容分类表的数据库实体（平铺，无父子树）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("cms_category")
public class Category implements Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 分类编码（唯一性代码层校验，软删行不占位）
     */
    private String code;
    /**
     * 内容类型 0缺省/1文章/11版本/12公告/13帮助/14提示
     */
    private Integer type;
    /**
     * 图标
     */
    private String icon;
    /**
     * 排序（升序，越小越前）
     */
    private Integer sort;
    /**
     * 是否启用：0 禁用，1 启用
     */
    private Integer enabled;
    /**
     * 描述
     */
    private String description;
    /**
     * 平台信息
     */
    @TableField(fill = FieldFill.INSERT)
    private String platform;
    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    private String tenantId;
    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 修改者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long modifyUser;
    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;
    /**
     * 删除状态：0 未删除，1 已删除
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteStatus;

}
