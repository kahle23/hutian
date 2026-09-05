package hutian.boot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置.
 * <p>
 * 开发态前端工程（hutian-web，默认 http://localhost:3000）直连后端，跨域统一在此放开；
 * 生产态前端已打进 fat jar 同源部署，本配置不影响.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600);
    }

}
