package hutian.system.pojo.param;

import kunlun.data.definition.annotation.FieldMeta;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 用户角色授权的入参对象（全量覆盖式授权）.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class UserGrantRoleParam implements Serializable {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @FieldMeta(description = "用户ID")
    private Long userId;
    /**
     * 角色ID集合（全量覆盖：传空集合表示清空该用户的所有角色授权）
     */
    @FieldMeta(description = "角色ID集合")
    private List<Long> roleIds;

}
