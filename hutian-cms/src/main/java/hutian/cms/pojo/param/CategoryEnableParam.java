package hutian.cms.pojo.param;

import kunlun.data.definition.annotation.FieldMeta;
import kunlun.data.json.support.jackson.annotation.JsonSceneDeserialize;
import kunlun.data.json.support.jackson.model.Scene;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 内容分类批量启用/禁用操作的入参对象.
 * <p>
 * baibao EnableDTO 为单条形状（recordId + enabled），不满足批量启停，故自建本对象.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
public class CategoryEnableParam implements Serializable {

    /**
     * 分类ID列表（批量）
     */
    @NotEmpty(message = "请选择要启停的分类！")
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    @FieldMeta(description = "分类ID列表")
    private List<Long> ids;
    /**
     * 启用/禁用状态值：0 禁用，1 启用
     */
    @NotNull(message = "启用/禁用状态值不能为空！")
    @FieldMeta(description = "启用/禁用状态值：0 禁用，1 启用")
    private Integer enabled;

}
