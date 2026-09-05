package hutian.system.pojo.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 当前登录用户的信息结果对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class CurrentUserResult implements Serializable {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 登录账号
     */
    private String account;
    /**
     * 姓名/昵称
     */
    private String name;
    /**
     * 头像地址
     */
    private String avatar;
    /**
     * 已授权的角色列表
     */
    private List<RoleSmpResult> roles;

}
