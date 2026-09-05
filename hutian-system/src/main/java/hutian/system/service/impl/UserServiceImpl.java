package hutian.system.service.impl;

import baibao.db.jdbc.mybatisplus.base.BaseServiceImpl;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import hutian.system.mapper.UserMapper;
import hutian.system.pojo.entity.User;
import hutian.system.pojo.param.UserAddParam;
import hutian.system.pojo.param.UserEditParam;
import hutian.system.pojo.param.UserResetPwdParam;
import hutian.system.pojo.param.UserUpdatePwdParam;
import hutian.system.pojo.query.UserQuery;
import hutian.system.pojo.result.UserResult;
import hutian.system.service.UserService;
import kunlun.crypto.CryptoUtil;
import kunlun.data.bean.BeanUtil;
import kunlun.exception.util.VerifyUtils;
import kunlun.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.util.StrUtil.isBlank;
import static cn.hutool.core.util.StrUtil.isNotBlank;
import static java.util.Objects.nonNull;

/**
 * 用户表的服务实现类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@Service
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User
        , UserAddParam, UserEditParam, UserQuery, UserResult> implements UserService {

    /**
     * 默认密码（新增/重置未指定密码时使用，摘要后入库）
     */
    private static final String DEFAULT_PASSWORD = "hutian@2026";

    @Override
    protected User fromAddParam(UserAddParam param) {
        // 账号唯一性校验（同 delete_status=0 范围内）
        VerifyUtils.isFalse(existByAccount(param.getAccount(), null)
                , "该账号已存在！");
        // 转换成实体
        User entity = BeanUtil.beanToBean(param, User.class);
        // 密码摘要：不传或为空用默认密码，一律走 kunlun CryptoUtil.digestToHex
        // （种子 admin123 即用此算法入库，绝不能用裸 SHA-256，否则 admin 永远登不上）
        String rawPassword = isBlank(param.getPassword()) ? DEFAULT_PASSWORD : param.getPassword();
        entity.setPassword(CryptoUtil.digestToHex(rawPassword));
        return entity;
    }

    @Override
    protected User fromEditParam(UserResult old, UserEditParam param) {
        // 账号唯一性校验（排除自身）——账号允许修改，但需保证全局唯一
        VerifyUtils.isFalse(existByAccount(param.getAccount(), param.getId())
                , "该账号已存在！");
        // 转换成实体（UserEditParam 不含 password，密码走独立接口，不会被覆盖）
        return BeanUtil.beanToBean(param, User.class);
    }

    @Override
    public boolean existByAccount(String account, Long id) {
        VerifyUtils.notBlank(account, "账号不能为空！");
        return count(Wrappers.lambdaQuery(User.class)
                .eq(User::getAccount, account)
                .ne(nonNull(id), User::getId, id)
        ) > 0;
    }

    @Override
    public User findByAccount(String account) {
        // 参数校验
        VerifyUtils.notBlank(account, "账号不能为空！");
        // 查询（全局逻辑删除自动过滤 delete_status=0）
        List<User> list = list(Wrappers.lambdaQuery(User.class)
                .eq(User::getAccount, account));
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        // uk_account 保证最多一条，多条属系统异常
        VerifyUtils.isTrue(list.size() == 1, "系统异常（ACCOUNT_NOT_UNIQUE）！");
        return list.get(0);
    }

    @Override
    public void resetPwd(UserResetPwdParam param) {
        // 参数校验
        VerifyUtils.notNull(param, "重置密码数据不能为空！");
        VerifyUtils.notNull(param.getId(), "用户ID不能为空！");
        // 新密码为空时重置为默认密码，摘要后入库
        String rawPassword = isBlank(param.getNewPassword()) ? DEFAULT_PASSWORD : param.getNewPassword();
        User entity = new User();
        entity.setId(param.getId());
        entity.setPassword(CryptoUtil.digestToHex(rawPassword));
        // 更新用户表数据（修改人会自动填充）
        VerifyUtils.isTrue(updateById(entity), "重置密码失败！");
    }

    @Override
    public void updatePwd(UserUpdatePwdParam param) {
        // 参数校验
        VerifyUtils.notNull(param, "修改密码数据不能为空！");
        VerifyUtils.notBlank(param.getOldPassword(), "旧密码不能为空！");
        VerifyUtils.notBlank(param.getNewPassword(), "新密码不能为空！");
        // 当前登录人从 kunlun SecurityUtil 取（不信任入参携带的用户标识）
        Long userId = SecurityUtil.getUserId((Long) null);
        User oldUser = getById(userId);
        VerifyUtils.notNull(oldUser, "当前登录用户不存在！");
        // 旧密码摘要比对，使用 MessageDigest.isEqual 防时序攻击
        // （两侧均为 kunlun CryptoUtil.digestToHex 产出的 hex，逐字节常量时间比较）
        String storedHex = oldUser.getPassword();
        String inputHex = CryptoUtil.digestToHex(param.getOldPassword());
        VerifyUtils.isTrue(
                MessageDigest.isEqual(
                        storedHex.getBytes(StandardCharsets.UTF_8),
                        inputHex.getBytes(StandardCharsets.UTF_8))
                , "原密码错误！");
        // 更新密码（摘要后入库，修改人会自动填充）
        User entity = new User();
        entity.setId(userId);
        entity.setPassword(CryptoUtil.digestToHex(param.getNewPassword()));
        VerifyUtils.isTrue(updateById(entity), "修改密码失败！");
    }

    @Override
    protected MPJLambdaWrapper<User> buildQueryWrapper(UserQuery query) {
        return JoinWrappers.lambda(User.class)
                .in(isNotEmpty(query.getIdList()), User::getId, query.getIdList())
                .like(isNotBlank(query.getAccountLike()), User::getAccount, query.getAccountLike())
                .like(isNotBlank(query.getNameLike()), User::getName, query.getNameLike())
                .eq(nonNull(query.getEnabled()), User::getEnabled, query.getEnabled())
                // 排序：id 降序（最新用户优先）
                .orderByDesc(User::getId)
        ;
    }

    @Override
    protected void processData(UserQuery query, List<UserResult> data) {
        // 用户列表暂无额外的数据处理
    }

    /**
     * 用户变动日志暂不接入（待系统域统一接入变动日志体系）.
     */
    @Override
    protected void changeLog(Object bizId, Object oldData, Object newData, Class<?> targetClz, Object... arguments) {
    }

}
