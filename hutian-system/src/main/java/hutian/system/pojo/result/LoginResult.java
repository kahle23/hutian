package hutian.system.pojo.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户登录的结果对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class LoginResult implements Serializable {

    /**
     * 登录令牌（JWT，后续请求放入 authorization 头）
     */
    private String token;

}
