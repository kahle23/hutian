package hutian.cms.pojo.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 内容生命周期留痕表的结果对象.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
public class ArticleLogResult implements Serializable {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 文章id
     */
    private Long articleId;
    /**
     * 动作：1 创建，2 发布，3 下线，4 置顶，5 取消置顶，6 编辑，7 复制
     */
    private Integer actionType;
    /**
     * 操作人ID（NULL=定时任务）
     */
    private Long operatorId;
    /**
     * 操作人姓名（回填：sys_user.name；定时任务为空）
     */
    private String operatorName;
    /**
     * 动作时间
     */
    private Date actionTime;
    /**
     * 详情文案
     */
    private String detail;
    /**
     * 创建时间
     */
    private Date createTime;

}
