package hutian.cms.pojo.query;

import baibao.common.dto.base.BaseQuery;
import kunlun.data.json.support.jackson.annotation.JsonSceneDeserialize;
import kunlun.data.json.support.jackson.model.Scene;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 内容表的查询对象（管理端列表与到期扫描共用）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArticleQuery extends BaseQuery implements Serializable {

    /**
     * 主键ID集合
     */
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    private List<Long> idList;
    /**
     * 分类id集合
     */
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    private List<Long> categoryIdList;
    /**
     * 内容类型集合：0 缺省，1 文章，11 版本，12 公告，13 帮助，14 提示
     */
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    private List<Integer> typeList;
    /**
     * 状态集合：0 草稿，1 已发布，2 已下线
     */
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    private List<Integer> statusList;
    /**
     * 标题（支持模糊搜索）
     */
    private String titleLike;
    /**
     * 发布时间-起
     */
    private Date publishTimeBegin;
    /**
     * 发布时间-止
     */
    private Date publishTimeEnd;
    /**
     * 下线时间-起
     */
    private Date expireTimeBegin;
    /**
     * 下线时间-止（到期下线扫描用）
     */
    private Date expireTimeEnd;

}
