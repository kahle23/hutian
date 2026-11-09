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
 * 内容已读表的数据库实体（触达统计与门户已读未读的数据源；一人一文一条）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("cms_article_read")
public class ArticleRead implements Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 文章id
     */
    private Long articleId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 首次阅读时间
     */
    private Date readTime;
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
