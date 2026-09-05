package hutian;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 洞天启动类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@EnableScheduling
@SpringBootApplication
@MapperScan(basePackages = {"hutian.*.mapper"})
public class HutianApplication {

    public static void main(String[] args) {
        SpringApplication.run(HutianApplication.class, args);
        log.info(">>> 洞天(hutian) 启动成功 <<<");
    }

}
