package hutian.basic.pojo.param;

import hutian.basic.enums.AsyncTaskStatus;
import hutian.basic.enums.AsyncTaskType;
import kunlun.core.annotation.FieldMeta;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 异步处理的任务表的编辑操作的入参对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
public class AsyncProcTaskEditParam implements Serializable {

    /**
     * 主键Id
     */
    @NotNull(message = "主键Id不能为空")
    @FieldMeta(description = "主键Id")
    private Long id;
    /**
     * 业务类型
     */
    @FieldMeta(description = "业务类型")
    private Integer businessType;
    /**
     * 业务ID
     */
    @FieldMeta(description = "业务ID")
    private Long businessId;
    /**
     * 任务类型
     * @see AsyncTaskType
     */
    @FieldMeta(description = "任务类型")
    private Integer taskType;
    /**
     * 任务参数
     */
    @FieldMeta(description = "任务参数")
    private String taskParameters;
    /**
     * 开始时间
     */
    @FieldMeta(description = "开始时间")
    private Date beginTime;
    /**
     * 结束时间
     */
    @FieldMeta(description = "结束时间")
    private Date endTime;
    /**
     * 原始文件
     */
    @FieldMeta(description = "原始文件")
    private String originalFiles;
    /**
     * 原文件名称
     */
    @FieldMeta(description = "原文件名称")
    private String originalFilename;
    /**
     * 状态
     * @see AsyncTaskStatus
     */
    @NotNull(message = "状态不能为空")
    @FieldMeta(description = "状态")
    private Integer status;
    /**
     * 执行者名称
     */
    @FieldMeta(description = "执行者名称")
    private String executorName;
    /**
     * 执行失败的次数
     */
    @FieldMeta(description = "执行失败的次数")
    private Integer execFailureCount;
    /**
     * 成功条数
     */
    @FieldMeta(description = "成功条数")
    private Integer successCount;
    /**
     * 失败条数
     */
    @FieldMeta(description = "失败条数")
    private Integer failureCount;
    /**
     * 总条数
     */
    @FieldMeta(description = "总条数")
    private Integer totalCount;
    /**
     * 结果文件信息
     */
    @FieldMeta(description = "结果文件信息")
    private String resultFile;
    /**
     * 错误信息
     */
    @FieldMeta(description = "错误信息")
    private String errorMessage;

}
