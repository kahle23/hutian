package hutian.basic.pojo.query;

import baibao.common.dto.base.BaseQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import kunlun.data.json.support.jackson.annotation.JsonSceneDeserialize;
import kunlun.data.json.support.jackson.model.Scene;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 运行日志表的查询对象.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RunLogQuery extends BaseQuery implements Serializable {

    /**
     * 主键ID
     */
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    private List<Long> idList;
    /**
     * 服务器的名称（支持模糊搜索）
     */
    private String serverNameLike;
    /**
     * 服务器的应用名称（支持模糊搜索）
     */
    private String serverAppNameLike;
    /**
     * 时间
     */
    private Date time;
    /**
     * 模块名（支持模糊搜索）
     */
    private String moduleLike;
    /**
     * 消息（支持模糊搜索）
     */
    private String messageLike;
    /**
     * 是否执行成功：0 未成功，1 成功
     */
    private Integer success;
    /**
     * 错误消息（支持模糊搜索）
     */
    private String errorLike;

}
