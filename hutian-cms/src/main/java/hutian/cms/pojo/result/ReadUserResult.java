package hutian.cms.pojo.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 内容已读/未读名单的结果对象（管理端触达统计）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
public class ReadUserResult implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户姓名（回填：sys_user.name）
     */
    private String userName;
    /**
     * 是否已读：0 未读，1 已读
     */
    private Integer isRead;
    /**
     * 首次阅读时间（未读名单无值）
     */
    private Date readTime;

}
