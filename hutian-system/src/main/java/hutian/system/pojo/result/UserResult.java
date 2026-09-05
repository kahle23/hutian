package hutian.system.pojo.result;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表的结果对象.
 * <p>
 * 绝不包含 password 字段：密码摘要仅用于登录校验，不对外回显。
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
@ExcelIgnoreUnannotated
public class UserResult implements Serializable {

    /**
     * 主键ID
     */
    @ExcelProperty("主键ID")
    private Long id;
    /**
     * 登录账号
     */
    @ExcelProperty("登录账号")
    private String account;
    /**
     * 姓名/昵称
     */
    @ExcelProperty("姓名/昵称")
    private String name;
    /**
     * 头像地址
     */
    @ExcelProperty("头像地址")
    private String avatar;
    /**
     * 是否启用：0 未启用，1 启用
     */
    @ExcelProperty("是否启用")
    private Integer enabled;
    /**
     * 平台
     */
    @ExcelProperty("平台")
    private String platform;
    /**
     * 租户标识
     */
    @ExcelProperty("租户标识")
    private String tenantId;
    /**
     * 创建人
     */
    @ExcelProperty("创建人")
    private String createUser;
    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @ExcelProperty("修改人")
    private String modifyUser;
    /**
     * 修改时间
     */
    @ExcelProperty("修改时间")
    private Date modifyTime;
    /**
     * 删除状态：0 未删除，1 已删除
     */
    @ExcelProperty("删除状态")
    private Integer deleteStatus;

}
