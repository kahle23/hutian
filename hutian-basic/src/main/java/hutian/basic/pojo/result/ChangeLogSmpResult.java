package hutian.basic.pojo.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据变动日志表的简单结果对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class ChangeLogSmpResult implements Serializable {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 时间
     */
    private Date time;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 用户的展示名
     */
    private String userDisplayName;
    /**
     * 用户所在的机构ID
     */
    private String orgId;
    /**
     * 用户所在的机构名称
     */
    private String orgName;
    /**
     * 平台信息
     */
    private String platform;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 业务ID
     */
    private String businessId;
    /**
     * 业务类型：0 缺省
     */
    private Integer businessType;
    /**
     * 消息
     */
    private String message;
    /**
     * 服务器的名称
     */
    private String serverName;
    /**
     * 服务器的应用名称
     */
    private String serverAppName;
    /**
     * 其他数据的JSON
     */
    private String dataJson;
    /**
     * 删除状态：0 未删除，1 已删除
     */
    private Integer deleteStatus;

}
