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
 * 内容表的数据库实体（文章/公告/版本/帮助/提示等所有内容共用一张表）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("cms_article")
public class Article implements Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 内容类型（同 cms_category.type）：0 缺省，1 文章，11 版本，12 公告，13 帮助，14 提示
     */
    private Integer type;
    /**
     * 分类id（NULL=未分类）
     */
    private Long categoryId;
    /**
     * 标题
     */
    private String title;
    /**
     * 封面图（JSON数组 [{name,addr}]）
     */
    private String coverImages;
    /**
     * 摘要（空则按正文提纯截前200字）
     */
    private String summary;
    /**
     * 置顶权重：0 不置顶；>0 置顶，值越大越靠前
     */
    private Integer topWeight;
    /**
     * 正文格式：0 缺省，1 纯文本，2 Markdown，3 Html
     */
    private Integer contentType;
    /**
     * 正文
     */
    private String content;
    /**
     * 附件（JSON数组 [{name,addr}]）
     */
    private String attachments;
    /**
     * 状态：0 草稿，1 已发布，2 已下线
     */
    private Integer status;
    /**
     * 发布时间（未来时间=定时发布）
     */
    private Date publishTime;
    /**
     * 发布人
     */
    private Long publishUser;
    /**
     * 下线时间（NULL=长期有效）
     */
    private Date expireTime;
    /**
     * 浏览数
     */
    private Integer viewCount;
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
