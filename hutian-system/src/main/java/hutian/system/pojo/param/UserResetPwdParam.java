package hutian.system.pojo.param;

import kunlun.data.definition.annotation.FieldMeta;
import kunlun.data.json.support.jackson.annotation.JsonSceneDeserialize;
import kunlun.data.json.support.jackson.model.Scene;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户表重置密码的入参对象（管理员重置）.
 * <p>
 * newPassword 可选：为空时重置为默认密码 hutian@2026，摘要后入库。
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class UserResetPwdParam implements Serializable {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空")
    @FieldMeta(description = "主键ID")
    private Long id;
    /**
     * 新密码（可选，为空时重置为默认密码 hutian@2026）
     */
    @FieldMeta(description = "新密码（为空时重置为默认密码 hutian@2026）")
    @JsonSceneDeserialize(Scene.STR_TRIM)
    private String newPassword;

}
