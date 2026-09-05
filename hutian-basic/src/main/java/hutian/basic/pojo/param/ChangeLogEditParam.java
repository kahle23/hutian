package hutian.basic.pojo.param;

import kunlun.core.annotation.FieldMeta;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 数据变动日志表的编辑操作的入参对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class ChangeLogEditParam implements Serializable {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空")
    @FieldMeta(description = "主键ID")
    private Long id;
    /**
     * 时间
     */
    @NotNull(message = "时间不能为空")
    @FieldMeta(description = "时间")
    private Date time;
    /**
     * 用户ID
     */
    @FieldMeta(description = "用户ID")
    private String userId;
    /**
     * 用户类型
     */
    @FieldMeta(description = "用户类型")
    private String userType;
    /**
     * 用户的展示名
     */
    @FieldMeta(description = "用户的展示名")
    private String userDisplayName;
    /**
     * 用户所在的机构ID
     */
    @FieldMeta(description = "用户所在的机构ID")
    private String orgId;
    /**
     * 用户所在的机构名称
     */
    @FieldMeta(description = "用户所在的机构名称")
    private String orgName;
    /**
     * 平台信息
     */
    @FieldMeta(description = "平台信息")
    private String platform;
    /**
     * 租户ID
     */
    @FieldMeta(description = "租户ID")
    private String tenantId;
    /**
     * 业务ID
     */
    @NotBlank(message = "业务ID不能为空")
    @FieldMeta(description = "业务ID")
    private String businessId;
    /**
     * 业务类型：0 缺省
     */
    @NotNull(message = "业务类型不能为空")
    @FieldMeta(description = "业务类型")
    private Integer businessType;
    /**
     * 消息
     */
    @FieldMeta(description = "消息")
    private String message;
    /**
     * 服务器的名称
     */
    @FieldMeta(description = "服务器的名称")
    private String serverName;
    /**
     * 服务器的应用名称
     */
    @FieldMeta(description = "服务器的应用名称")
    private String serverAppName;
    /**
     * 其他数据的JSON
     */
    @FieldMeta(description = "其他数据的JSON")
    private String dataJson;
    /**
     * 删除状态：0 未删除，1 已删除
     */
    @FieldMeta(description = "删除状态：0 未删除，1 已删除")
    private Integer deleteStatus;

}
