package hutian.system.pojo.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户表的简单结果对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class UserSmpResult implements Serializable {

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
     * 是否启用：0 未启用，1 启用
     */
    private Integer enabled;

}
