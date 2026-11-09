package hutian.system.config;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * hutian-system 作为依赖引入时的 Spring 自动装配.
 * <p>
 * 宿主工程根包不在 hutian 下时，靠本类（经 META-INF/spring.factories 注册）
 * 完成组件扫描与 Mapper 扫描，引 jar 即生效，无需宿主额外配置；
 * hutian 自身启动类已扫 hutian.*，重复扫描由 Spring 去重，互不影响.
 * <p>
 * 分页处理器、审计字段填充、令牌管理器三样平台基建由宿主工程装配
 * （hutian 平台 = hutian-boot 统一提供；外部宿主需自行提供等价基建）.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@Configuration
@ComponentScan({"hutian.system"})
@MapperScan(basePackages = {"hutian.system.mapper"})
public class SystemServiceAutoConfiguration {

    public SystemServiceAutoConfiguration() {

        log.info("The plugin \"hutian-system\" is enabled successfully. ");
    }

}
