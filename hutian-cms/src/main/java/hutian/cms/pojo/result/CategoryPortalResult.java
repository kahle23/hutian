package hutian.cms.pojo.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 门户分类文件夹的结果对象.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
public class CategoryPortalResult implements Serializable {

    /**
     * 分类ID
     */
    private Long id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 分类图标
     */
    private String icon;
    /**
     * 分类描述
     */
    private String description;
    /**
     * 排序（升序，越小越前）
     */
    private Integer sort;
    /**
     * 该分类下用户端可见内容数（已发布且有效期内）
     */
    private Long articleCount;

}
