package hutian.boot.controller;

import kunlun.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康检查 前端控制器.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@RestController
public class PingController {

    @Resource
    private Environment environment;

    @GetMapping("/base/ping")
    public Result<Map<String, Object>> ping() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("name", "hutian");
        data.put("desc", "壶天 - 炼器成天：装得下多方天地的应用平台，也是组件淬炼与诞生之地");
        data.put("profile", environment.getActiveProfiles());
        data.put("time", LocalDateTime.now().toString());
        return Result.success(data);
    }

}
