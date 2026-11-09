package hutian.cms.pojo.param;

import kunlun.data.definition.annotation.FieldMeta;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 内容生命周期留痕新增操作的入参对象（框架泛型占位；留痕统一走 writeLog 内部写入）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
public class ArticleLogAddParam implements Serializable {

    /**
     * 文章id
     */
    @NotNull(message = "文章ID不能为空！")
    @FieldMeta(description = "文章ID")
    private Long articleId;
    /**
     * 动作：1 创建，2 发布，3 下线，4 置顶，5 取消置顶，6 编辑，7 复制
     */
    @NotNull(message = "动作类型不能为空！")
    @FieldMeta(description = "动作类型")
    private Integer actionType;
    /**
     * 操作人（NULL=定时任务）
     */
    @FieldMeta(description = "操作人ID")
    private Long operatorId;
    /**
     * 动作时间
     */
    @FieldMeta(description = "动作时间")
    private Date actionTime;
    /**
     * 详情文案
     */
    @FieldMeta(description = "详情文案")
    private String detail;

}
