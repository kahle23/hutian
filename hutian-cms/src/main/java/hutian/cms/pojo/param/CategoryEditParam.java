package hutian.cms.pojo.param;

import baibao.common.dto.base.BaseEditParam;
import kunlun.data.definition.annotation.FieldMeta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 内容分类编辑操作的入参对象.
 * <p>
 * 编辑清空编码时提交 clearFields:["code"]（置空子句与更新合并为同一条 UPDATE）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CategoryEditParam extends BaseEditParam implements Serializable {

    /**
     * 主键ID
     */
    @NotNull(message = "分类ID不能为空！")
    @FieldMeta(description = "分类ID")
    private Long id;
    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空！")
    @Size(max = 100, message = "分类名称长度不能超过100！")
    @FieldMeta(description = "分类名称")
    private String name;
    /**
     * 分类编码（选填；非空时代码层唯一校验；清空走 clearFields）
     */
    @Size(max = 64, message = "分类编码长度不能超过64！")
    @FieldMeta(description = "分类编码，选填")
    private String code;
    /**
     * 内容类型 0缺省/1文章/11版本/12公告/13帮助/14提示（传了才校验修改）
     */
    @FieldMeta(description = "内容类型")
    private Integer type;
    /**
     * 图标
     */
    @FieldMeta(description = "图标")
    private String icon;
    /**
     * 排序（升序，越小越前）
     */
    @FieldMeta(description = "排序，升序越小越前")
    private Integer sort;
    /**
     * 是否启用：0 禁用，1 启用
     */
    @FieldMeta(description = "是否启用：0 禁用，1 启用")
    private Integer enabled;
    /**
     * 描述
     */
    @Size(max = 255, message = "描述长度不能超过255！")
    @FieldMeta(description = "描述")
    private String description;

}
