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
 * 内容生命周期留痕表的数据库实体.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("cms_article_log")
public class ArticleLog implements Serializable {

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
     * 动作：1 创建，2 发布，3 下线，4 置顶，5 取消置顶，6 编辑，7 复制
     */
    private Integer actionType;
    /**
     * 操作人（NULL=定时任务）
     */
    private Long operatorId;
    /**
     * 动作时间
     */
    private Date actionTime;
    /**
     * 详情文案
     */
    private String detail;
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
