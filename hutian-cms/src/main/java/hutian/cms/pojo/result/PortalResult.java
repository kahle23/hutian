package hutian.cms.pojo.result;

import baibao.common.dto.FileDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 内容用户端（门户）的结果对象.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
public class PortalResult implements Serializable {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 内容类型：0 缺省，1 文章，11 版本，12 公告，13 帮助，14 提示
     */
    private Integer type;
    /**
     * 分类id（NULL=未分类）
     */
    private Long categoryId;
    /**
     * 分类名称（回填：cms_category.name）
     */
    private String categoryName;
    /**
     * 标题
     */
    private String title;
    /**
     * 封面图（service 从库存 JSON 反序列化回填；字段名避开实体同名 String 属性，防止框架按名拷贝时类型冲突）
     */
    @JsonProperty("coverImages")
    private List<FileDTO> coverImageList;
    /**
     * 摘要
     */
    private String summary;
    /**
     * 正文（按 contentType 口径，仅详情返回）
     */
    private String content;
    /**
     * 正文格式：0 缺省，1 纯文本，2 Markdown，3 Html
     */
    private Integer contentType;
    /**
     * 附件（service 从库存 JSON 反序列化回填，仅详情返回；字段名避开实体同名 String 属性，防止框架按名拷贝时类型冲突）
     */
    @JsonProperty("attachments")
    private List<FileDTO> attachmentList;
    /**
     * 置顶权重：0 不置顶；>0 置顶，值越大越靠前
     */
    private Integer topWeight;
    /**
     * 发布时间
     */
    private Date publishTime;
    /**
     * 下线时间（NULL=长期有效）
     */
    private Date expireTime;
    /**
     * 发布人ID
     */
    private Long publishUser;
    /**
     * 发布人姓名（回填：sys_user.name）
     */
    private String publishUserName;
    /**
     * 浏览数
     */
    private Integer viewCount;
    /**
     * 当前用户是否已读：0 未读，1 已读
     */
    private Integer isRead;
    /**
     * 当前用户的首次阅读时间（仅「我的已读」返回）
     */
    private Date readTime;
    /**
     * 命中片段（仅全文搜索且正文命中时返回：正文剥标记后取首个命中关键字前后各 60 字；
     * 仅标题/摘要命中时为空，前端回落摘要展示）
     */
    private String snippet;

}
