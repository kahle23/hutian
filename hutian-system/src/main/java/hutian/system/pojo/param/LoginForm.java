package hutian.system.pojo.param;

import kunlun.core.annotation.FieldMeta;
import kunlun.data.json.support.jackson.annotation.JsonSceneDeserialize;
import kunlun.data.json.support.jackson.model.Scene;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户登录的入参对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class LoginForm implements Serializable {

    /**
     * 登录账号
     */
    @NotBlank(message = "账号不能为空")
    @FieldMeta(description = "登录账号")
    @JsonSceneDeserialize(Scene.STR_TRIM)
    private String account;
    /**
     * 登录密码（明文，服务端摘要后与库内摘要比对）
     */
    @NotBlank(message = "密码不能为空")
    @FieldMeta(description = "登录密码")
    @JsonSceneDeserialize(Scene.STR_TRIM)
    private String password;

}
