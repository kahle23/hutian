package hutian.system.pojo.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单表的树形结果对象.
 * <p>
 * 在 {@link MenuSmpResult} 基础上增加 children 子节点集合，用于菜单树/权限树组装.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MenuTreeResult extends MenuSmpResult implements Serializable {

    /**
     * 子菜单集合
     */
    private List<MenuTreeResult> children;

}
