package hutian.cms.pojo.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 门户分类文件夹列表的结果对象.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
public class CategoryPortalListResult implements Serializable {

    /**
     * 该类型用户端可见内容总数（"全部"文件夹角标；含分类被禁用/删除的悬挂内容）
     */
    private Long totalCount;
    /**
     * 未分类（category_id 为空）可见内容数（前端仅在该值大于 0 时展示"未分类"文件夹）
     */
    private Long uncategorizedCount;
    /**
     * 分类文件夹列表（启用分类且类型精确匹配，sort 升序）
     */
    private List<CategoryPortalResult> categoryList;

}
