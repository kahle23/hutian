package hutian.system.pojo.result;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 菜单表的结果对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
@ExcelIgnoreUnannotated
public class MenuResult implements Serializable {

    /**
     * 主键ID
     */
    @ExcelProperty("主键ID")
    private Long id;
    /**
     * 父菜单ID（0=根）
     */
    @ExcelProperty("父菜单ID")
    private Long parentId;
    /**
     * 菜单名称
     */
    @ExcelProperty("菜单名称")
    private String name;
    /**
     * 菜单类型：1 目录，2 菜单，3 按钮
     */
    @ExcelProperty("菜单类型")
    private Integer type;
    /**
     * 菜单描述
     */
    @ExcelProperty("菜单描述")
    private String description;
    /**
     * 图标
     */
    @ExcelProperty("图标")
    private String icon;
    /**
     * 前端路由地址
     */
    @ExcelProperty("前端路由地址")
    private String route;
    /**
     * 前端组件路径
     */
    @ExcelProperty("前端组件路径")
    private String component;
    /**
     * 权限标识
     */
    @ExcelProperty("权限标识")
    private String permission;
    /**
     * 排序（越小越靠前）
     */
    @ExcelProperty("排序")
    private Integer sort;
    /**
     * 是否启用：0 未启用，1 启用
     */
    @ExcelProperty("是否启用")
    private Integer enabled;
    /**
     * 是否显示：0 隐藏，1 显示
     */
    @ExcelProperty("是否显示")
    private Integer showFlag;
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
