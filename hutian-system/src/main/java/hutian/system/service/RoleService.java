package hutian.system.service;

import baibao.db.jdbc.mybatisplus.base.BaseService;
import hutian.system.pojo.entity.Role;
import hutian.system.pojo.param.RoleAddParam;
import hutian.system.pojo.param.RoleEditParam;
import hutian.system.pojo.query.RoleQuery;
import hutian.system.pojo.result.RoleResult;

/**
 * 角色表的服务类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
public interface RoleService extends BaseService<Role
        , RoleAddParam, RoleEditParam, RoleQuery, RoleResult> {

    /**
     * 判断角色名称是否已存在（排除掉输入的ID，仅在未删除范围内判断）.
     *
     * @param name 待判断的角色名称
     * @param id   需要被排除的ID（可以为空，新增时传空）
     * @return 存在返回 true，否则 false
     */
    boolean existByName(String name, Long id);

}
