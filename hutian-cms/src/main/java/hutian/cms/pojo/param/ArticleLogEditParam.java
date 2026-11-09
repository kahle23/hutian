package hutian.cms.pojo.param;

import baibao.common.dto.base.BaseEditParam;
import kunlun.data.definition.annotation.FieldMeta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 内容生命周期留痕编辑操作的入参对象（框架泛型占位；留痕表只读，不开放编辑入口）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArticleLogEditParam extends BaseEditParam implements Serializable {

    /**
     * 主键ID
     */
    @NotNull(message = "留痕ID不能为空！")
    @FieldMeta(description = "留痕ID")
    private Long id;
    /**
     * 详情文案
     */
    @FieldMeta(description = "详情文案")
    private String detail;
    /**
     * 动作时间
     */
    @FieldMeta(description = "动作时间")
    private Date actionTime;

}
