package hutian.cms.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import hutian.cms.pojo.entity.SysUser;

/**
 * 用户表的只读 Mapper 接口（跨域读 sys_user：用户名回填/触达目标/未读名单，禁止写操作）.
 * <p>
 * 实体为 {@link hutian.cms.pojo.entity.SysUser} 只读影子映射，详见其类注释.
 *
 * @author Hutian
 * @since 2026-11-06
 */
public interface SysUserMapper extends MPJBaseMapper<SysUser> {

}
