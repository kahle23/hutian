package hutian.system.pojo.result;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色表的结果对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
@ExcelIgnoreUnannotated
public class RoleResult implements Serializable {

    /**
     * 主键ID
     */
    @ExcelProperty("主键ID")
    private Long id;
    /**
     * 角色名称
     */
    @ExcelProperty("角色名称")
    private String name;
    /**
     * 角色编码
     */
    @ExcelProperty("角色编码")
    private String code;
    /**
     * 描述
     */
    @ExcelProperty("描述")
    private String description;
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
