package hutian.system.pojo.param;

import kunlun.core.annotation.FieldMeta;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 角色表的编辑操作的入参对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class RoleEditParam implements Serializable {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空")
    @FieldMeta(description = "主键ID")
    private Long id;
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
