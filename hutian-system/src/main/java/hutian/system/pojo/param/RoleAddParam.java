package hutian.system.pojo.param;

import kunlun.data.definition.annotation.FieldMeta;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 角色表的新增操作的入参对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class RoleAddParam implements Serializable {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @FieldMeta(description = "角色名称")
    private String name;
    /**
     * 角色编码
     */
    @FieldMeta(description = "角色编码")
    private String code;
    /**
     * 描述
     */
    @FieldMeta(description = "描述")
    private String description;
    /**
     * 是否启用：0 未启用，1 启用
     */
    @NotNull(message = "是否启用不能为空")
    @FieldMeta(description = "是否启用")
    private Integer enabled;

}
