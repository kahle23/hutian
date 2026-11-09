package hutian.system.pojo.param;

import kunlun.data.definition.annotation.FieldMeta;
import kunlun.data.json.support.jackson.annotation.JsonSceneDeserialize;
import kunlun.data.json.support.jackson.model.Scene;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 用户表的新增操作的入参对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class UserAddParam implements Serializable {

    /**
     * 登录账号
     */
    @NotBlank(message = "账号不能为空")
    @Size(max = 64, message = "账号长度不能超过64")
    @FieldMeta(description = "登录账号")
    @JsonSceneDeserialize(Scene.STR_TRIM)
    private String account;
    /**
     * 密码（可选，不传或为空时入库使用默认密码 hutian@2026，摘要后入库）
     */
    @FieldMeta(description = "密码（可选，不传用默认密码 hutian@2026）")
    @JsonSceneDeserialize(Scene.STR_TRIM)
    private String password;
    /**
     * 姓名/昵称
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 64, message = "姓名长度不能超过64")
    @FieldMeta(description = "姓名/昵称")
    @JsonSceneDeserialize(Scene.STR_TRIM)
    private String name;
    /**
     * 头像地址
     */
    @FieldMeta(description = "头像地址")
    private String avatar;
    /**
     * 是否启用：0 未启用，1 启用
     */
    @NotNull(message = "是否启用不能为空")
    @FieldMeta(description = "是否启用")
    private Integer enabled;

}
