package hutian.cms.pojo.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 内容分类表的结果对象.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
public class CategoryResult implements Serializable {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 分类编码（唯一性代码层校验，软删行不占位）
     */
    private String code;
    /**
     * 内容类型 0缺省/1文章/11版本/12公告/13帮助/14提示
     */
    private Integer type;
    /**
     * 图标
     */
    private String icon;
    /**
     * 排序（升序，越小越前）
     */
    private Integer sort;
    /**
     * 是否启用：0 禁用，1 启用
     */
    private Integer enabled;
    /**
     * 描述
     */
    private String description;
    /**
     * 该分类未删文章数（列表口径批量计数回填；详情冗余字段，前端可忽略）
     */
    private Long articleCount;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date modifyTime;

}
