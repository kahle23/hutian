package hutian.boot.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置.
 * <p>
 * 开发态前端工程（hutian-web，默认 http://localhost:3000）直连后端，跨域统一在此放开；
 * 生产态前端已打进 fat jar 同源部署，本配置不影响.
 * <p>
 * 用 Servlet 层 {@link CorsFilter}（最高优先级）而非 WebMvcConfigurer#addCorsMappings：
 * 预检 OPTIONS 请求在过滤器层短路完成，不会落到 kunlun 安全拦截器被 401 拦截
 * （否则预检响应无 ACAO 头，浏览器直接判定跨域失败——BUG-BASE-001）.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Configuration
public class CorsConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean =
                new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

}
