package hutian.cms.pojo.query;

import baibao.common.dto.base.BaseQuery;
import kunlun.data.json.support.jackson.annotation.JsonSceneDeserialize;
import kunlun.data.json.support.jackson.model.Scene;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 内容用户端（门户）的查询对象.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PortalQuery extends BaseQuery implements Serializable {

    /**
     * 分类id（可选；传 0 表示未分类——仅查 category_id 为空的内容）
     */
    private Long categoryId;
    /**
     * 内容类型集合：0 缺省，1 文章，11 版本，12 公告，13 帮助，14 提示
     */
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    private List<Integer> typeList;
    /**
     * 关键字（模糊查询标题；fullText=true 时同时匹配标题/摘要/正文）
     */
    private String keyword;
    /**
     * 全文搜索开关：true 时 keyword 同时匹配标题/摘要/正文（正文命中时出参带 snippet 片段）；
     * 默认 false 仅匹配标题
     */
    private Boolean fullText = Boolean.FALSE;

}
