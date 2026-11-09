package hutian.boot.config;

import kunlun.security.TokenManager;
import kunlun.security.support.JwtTokenManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * hutian 平台基建装配（自 hutian-starter 的自动装配迁入，改由宿主工程直接扫描生效）.
 * <p>
 * 外部宿主引入 hutian 业务组件时，需自行提供等价基建：
 * 分页处理器注册（见 {@link PagingConfig}）、审计字段填充（见 {@link HutianMetaObjectHandler}）、
 * 令牌管理器（见本类 tokenManager）.
 *
 * @author Hutian
 * @since 2026-09-06
 */
@Slf4j
@Configuration
public class HutianInfraConfig {

    public HutianInfraConfig() {

        log.info("The hutian infra config (TokenManager etc.) is registered successfully. ");
    }

    /**
     * 令牌管理器：JWT 无状态实现，有效期 30 天.
     * <p>
     * baibao 的 SecurityAutoConfiguration 默认实现引用 StringRedisTemplate，
     * 不引 Redis 的环境类加载即失败，此处注册无 Redis 依赖的 JWT 版本；
     * 宿主自定义 TokenManager bean 时本定义让位.
     */
    @Bean
    @ConditionalOnMissingBean(TokenManager.class)
    public TokenManager tokenManager() {

        return new JwtTokenManager(null, 30 * 24 * 60 * 60L);
    }

}
