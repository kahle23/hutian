package hutian.cms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户表的只读影子实体（仅映射 cms 域跨表要用的少量列，禁止写操作）.
 * <p>
 * hutian-cms 按模板仅依赖 hutian-common，不引 hutian-system（避免模块间反向耦合）；
 * 用户名回填、触达目标人数、未读名单等需要读 sys_user，故在此提供最小只读映射，
 * 表结构与 hutian-system 的 User 实体同源（docs/sql/system.sql），
 * 逻辑删除字段 delete_status 由全局配置自动过滤.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class SysUser implements Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 登录账号
     */
    private String account;
    /**
     * 姓名/昵称
     */
    private String name;
    /**
     * 是否启用：0 未启用，1 启用
     */
    private Integer enabled;
    /**
     * 删除状态：0 未删除，1 已删除（只读映射，逻辑删除自动过滤）
     */
    private Integer deleteStatus;

}
