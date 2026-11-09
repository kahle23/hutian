package hutian.cms.pojo.param;

import baibao.common.dto.FileDTO;
import kunlun.data.definition.annotation.FieldMeta;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 内容新增操作的入参对象.
 * <p>
 * status=1 直接发布（补 publishTime/publishUser）；publishTime 为未来时间时按约定
 * 必须走草稿 + /publish 登记（服务端强制回落草稿，防止定时意图被立即发布）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
public class ArticleAddParam implements Serializable {

    /**
     * 内容类型：0 缺省，1 文章，11 版本，12 公告，13 帮助，14 提示（为空默认 1 文章）
     */
    @FieldMeta(description = "内容类型，为空默认 1 文章")
    private Integer type;
    /**
     * 分类id（为空表示未分类）
     */
    @FieldMeta(description = "分类id，为空表示未分类")
    private Long categoryId;
    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空！")
    @Size(max = 200, message = "标题长度不能超过200！")
    @FieldMeta(description = "标题")
    private String title;
    /**
     * 封面图（手工登记 URL：{name,addr} 列表，JSON 后入库）
     */
    @FieldMeta(description = "封面图列表")
    private List<FileDTO> coverImages;
    /**
     * 摘要（空则按正文提纯截前200字）
     */
    @Size(max = 500, message = "摘要长度不能超过500！")
    @FieldMeta(description = "摘要，留空则按正文自动提取")
    private String summary;
    /**
     * 置顶权重：0 不置顶；>0 置顶，值越大越靠前
     */
    @FieldMeta(description = "置顶权重：0 不置顶，大于0置顶")
    private Integer topWeight;
    /**
     * 正文格式：1 纯文本，2 Markdown，3 Html（非法值回落 2）
     */
    @FieldMeta(description = "正文格式：1 纯文本，2 Markdown，3 Html")
    private Integer contentType;
    /**
     * 正文
     */
    @FieldMeta(description = "正文")
    private String content;
    /**
     * 附件（手工登记 URL：{name,addr} 列表，JSON 后入库）
     */
    @FieldMeta(description = "附件列表")
    private List<FileDTO> attachments;
    /**
     * 状态：0 草稿，1 直接发布（为空默认草稿）
     */
    @FieldMeta(description = "状态：0 草稿，1 直接发布")
    private Integer status;
    /**
     * 发布时间（为未来时间=定时发布：强制保持草稿，由定时任务到点发布）
     */
    @FieldMeta(description = "发布时间，未来时间表示定时发布")
    private Date publishTime;
    /**
     * 下线时间（有效期截止）；为空表示长期有效
     */
    @FieldMeta(description = "下线时间，为空表示长期有效")
    private Date expireTime;

}
