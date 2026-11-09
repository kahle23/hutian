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
 * 用户表的编辑操作的入参对象.
 * <p>
 * 不含 password 字段：密码走独立的 reset-pwd / update-pwd 接口。
 * 账号允许修改，但在 service 层做唯一性校验（排除自身）。
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class UserEditParam implements Serializable {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空")
    @FieldMeta(description = "主键ID")
    private Long id;
    /**
     * 登录账号
     */
    @NotBlank(message = "账号不能为空")
    @Size(max = 64, message = "账号长度不能超过64")
    @FieldMeta(description = "登录账号")
    @JsonSceneDeserialize(Scene.STR_TRIM)
    private String account;
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
