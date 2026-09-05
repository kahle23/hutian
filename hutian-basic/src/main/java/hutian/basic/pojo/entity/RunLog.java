package hutian.basic.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 运行日志表的数据库实体.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("base_run_log")
public class RunLog implements Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 服务器的名称
     */
    private String serverName;
    /**
     * 服务器的应用名称
     */
    private String serverAppName;
    /**
     * 时间
     */
    private Date time;
    /**
     * 模块名
     */
    private String module;
    /**
     * 消息
     */
    private String message;
    /**
     * 是否执行成功：0 未成功，1 成功
     */
    private Integer success;
    /**
     * 错误消息
     */
    private String error;
    /**
     * 其他数据的JSON
     */
    private String dataJson;
    /**
     * 删除状态：0 未删除，1 已删除
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteStatus;

}
