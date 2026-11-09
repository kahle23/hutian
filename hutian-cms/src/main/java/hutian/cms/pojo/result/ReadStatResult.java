package hutian.cms.pojo.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 内容触达统计的结果对象（管理端详情）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
public class ReadStatResult implements Serializable {

    /**
     * 目标人数（启用状态的用户数）
     */
    private Integer targetCount;
    /**
     * 已读人数
     */
    private Integer readCount;
    /**
     * 未读人数
     */
    private Integer unreadCount;
    /**
     * 触达率（已读÷目标人数，百分比保留 1 位小数；目标为 0 时为 0）
     */
    private Double reachRate;

}
