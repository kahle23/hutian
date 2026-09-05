package hutian.base.config;

import kunlun.security.TokenManager;
import kunlun.security.support.JwtTokenManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * hutian-base 作为依赖引入时的 Spring 自动装配.
 * <p>
 * 宿主工程根包不在 hutian 下时，靠本类（经 META-INF/spring.factories 注册）
 * 完成组件扫描，引 jar 即获得审计字段填充、分页基建与令牌管理器，无需宿主额外配置；
 * hutian 自身启动类已扫 hutian.*，重复扫描由 Spring 去重，互不影响.
 * <p>
 * 宿主配置 hutian.base.enabled=false 可整体关闭本模块的自动装配.
 *
 * @author Hutian
 * @since 2026-09-06
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "hutian.base", name = "enabled", matchIfMissing = true)
@ComponentScan({"hutian.base"})
public class BaseAutoConfiguration {

    public BaseAutoConfiguration() {

        log.info("The plugin \"hutian-base\" is enabled successfully. ");
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
