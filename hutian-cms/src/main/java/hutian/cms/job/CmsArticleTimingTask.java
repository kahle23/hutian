package hutian.cms.job;

import hutian.cms.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 内容定时上下线的定时任务（定时发布到点置为已发布、有效期到期自动下线）.
 * <p>
 * 单机部署不加分布式锁；异常 catch 后 log.error（hutian 无运行日志事件处理器，不落库）；
 * @EnableScheduling 已由 hutian-boot 启动类开启.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Slf4j
@Component
public class CmsArticleTimingTask {

    @Resource
    private ArticleService articleService;

    /**
     * 定时发布：每分钟第 0 秒扫描到点草稿并发布（operator NULL，详情见 publishDue）.
     */
    @Scheduled(cron = "0 * * * * ?")
    public void publishDue() {
        try {
            // 扫描 publish_time 已到且仍为草稿的内容并发布
            int count = articleService.publishDue();
            if (count > 0) {
                log.info("内容定时发布任务完成，本次发布 {} 条内容", count);
            }
        }
        catch (Exception e) {
            log.error("内容定时发布任务执行失败！", e);
        }
    }

    /**
     * 到期下线：每分钟第 30 秒扫描有效期已过的已发布内容并下线.
     */
    @Scheduled(cron = "30 * * * * ?")
    public void expireDue() {
        try {
            // 扫描 expire_time 已到且仍为已发布的内容并下线
            int count = articleService.expireDue();
            if (count > 0) {
                log.info("内容到期下线任务完成，本次下线 {} 条内容", count);
            }
        }
        catch (Exception e) {
            log.error("内容到期下线任务执行失败！", e);
        }
    }

}
