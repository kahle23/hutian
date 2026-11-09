package hutian.cms.pojo.param;

import kunlun.data.definition.annotation.FieldMeta;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 内容置顶操作的入参对象（置顶/取消置顶）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
public class ArticleTopParam implements Serializable {

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空！")
    @FieldMeta(description = "内容ID")
    private Long id;
    /**
     * 置顶权重：0 取消置顶；>0 置顶，值越大越靠前（负数拦截）
     */
    @NotNull(message = "置顶权重不能为空！")
    @FieldMeta(description = "置顶权重：0 取消置顶；大于0置顶")
    private Integer topWeight;

}
