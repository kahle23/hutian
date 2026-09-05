package hutian.system.service;

import baibao.db.jdbc.mybatisplus.base.BaseService;
import hutian.system.pojo.entity.User;
import hutian.system.pojo.param.UserAddParam;
import hutian.system.pojo.param.UserEditParam;
import hutian.system.pojo.param.UserResetPwdParam;
import hutian.system.pojo.param.UserUpdatePwdParam;
import hutian.system.pojo.query.UserQuery;
import hutian.system.pojo.result.UserResult;

/**
 * 用户表的服务类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
public interface UserService extends BaseService<User
        , UserAddParam, UserEditParam, UserQuery, UserResult> {

    /**
     * 根据账号查询用户信息（登录用，仅在未删除范围内判断）.
     * <p>
     * 如果查到多条将会报错（账号唯一性由 uk_account 保证，多条属系统异常）。
     *
     * @param account 待查询的账号
     * @return 查询到的用户信息或者 null
     */
    User findByAccount(String account);

    /**
     * 重置密码（管理员重置）.
     * <p>
     * 入参 newPassword 为空时重置为默认密码 hutian@2026，摘要后入库。
     *
     * @param param 重置密码入参（id + newPassword）
     */
    void resetPwd(UserResetPwdParam param);

    /**
     * 当前登录人修改密码（本人修改）.
     * <p>
     * 当前登录人从 kunlun SecurityUtil 取（userId），不信任入参携带的用户标识；
     * 旧密码摘要比对使用 MessageDigest.isEqual 防时序攻击。
     *
     * @param param 修改密码入参（oldPassword + newPassword）
     */
    void updatePwd(UserUpdatePwdParam param);

    /**
     * 判断账号是否已存在（排除掉输入的ID，仅在未删除范围内判断）.
     *
     * @param account 待判断的账号
     * @param id      需要被排除的ID（可以为空，新增时传空）
     * @return 存在返回 true，否则 false
     */
    boolean existByAccount(String account, Long id);

}
