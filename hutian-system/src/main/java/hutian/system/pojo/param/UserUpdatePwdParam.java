package hutian.system.pojo.param;

import kunlun.core.annotation.FieldMeta;
import kunlun.data.json.support.jackson.annotation.JsonSceneDeserialize;
import kunlun.data.json.support.jackson.model.Scene;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户表修改密码的入参对象（本人修改）.
 * <p>
 * 当前登录人从 kunlun SecurityUtil 取（userId），不信任入参携带的用户标识。
 * 旧密码摘要比对使用 MessageDigest.isEqual 防时序攻击。
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class UserUpdatePwdParam implements Serializable {

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    @FieldMeta(description = "旧密码")
    @JsonSceneDeserialize(Scene.STR_TRIM)
    private String oldPassword;
    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @FieldMeta(description = "新密码")
    @JsonSceneDeserialize(Scene.STR_TRIM)
    private String newPassword;

}
