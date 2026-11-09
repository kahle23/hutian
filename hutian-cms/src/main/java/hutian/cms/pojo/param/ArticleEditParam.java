package hutian.cms.pojo.param;

import baibao.common.dto.FileDTO;
import baibao.common.dto.base.BaseEditParam;
import kunlun.data.definition.annotation.FieldMeta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 内容编辑操作的入参对象（不含 status/publishTime，发布走 /publish 专门接口）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArticleEditParam extends BaseEditParam implements Serializable {

    /**
     * 主键ID
     */
    @NotNull(message = "内容ID不能为空！")
    @FieldMeta(description = "内容ID")
    private Long id;
    /**
     * 内容类型：0 缺省，1 文章，11 版本，12 公告，13 帮助，14 提示（传了才校验修改）
     */
    @FieldMeta(description = "内容类型")
    private Integer type;
    /**
     * 分类id（为空表示未分类，不传则沿用原值）
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
     * 封面图（手工登记 URL：{name,addr} 列表，JSON 后入库；不传沿用原值）
     */
    @FieldMeta(description = "封面图列表")
    private List<FileDTO> coverImages;
    /**
     * 摘要（空且有新正文时按正文提纯截前200字）
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
     * 附件（手工登记 URL：{name,addr} 列表，JSON 后入库；不传沿用原值）
     */
    @FieldMeta(description = "附件列表")
    private List<FileDTO> attachments;

}
