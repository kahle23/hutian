package hutian.cms.pojo.query;

import baibao.common.dto.base.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 内容已读/未读名单的查询对象（触达名单端点的分页入参；名单范围由路径上的 recordId 决定）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArticleReadQuery extends BaseQuery implements Serializable {

}
