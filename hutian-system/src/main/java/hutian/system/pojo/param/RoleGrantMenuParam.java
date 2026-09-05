package hutian.system.pojo.param;

import kunlun.core.annotation.FieldMeta;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 角色菜单授权的入参对象（全量覆盖式授权）.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class RoleGrantMenuParam implements Serializable {

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    @FieldMeta(description = "角色ID")
    private Long roleId;
    /**
     * 菜单ID集合（全量覆盖：传空集合表示清空该角色的所有菜单授权）
     */
    @FieldMeta(description = "菜单ID集合")
    private List<Long> menuIds;

}
