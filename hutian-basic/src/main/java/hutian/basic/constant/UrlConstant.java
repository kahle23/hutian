package hutian.basic.constant;

/**
 * URL 常量.
 * <p>
 * 与前端 api 层的路径保持一致，basic 域新增 URL 统一在此登记.
 *
 * @author Hutian
 * @since 2026-09-06
 */
public interface UrlConstant {

    /** 模块统一前缀 - basic（基础服务域：日志/异步任务）. */
    String PREFIX = "/basic";

    /** 操作日志. */
    String OP_LOG = PREFIX + "/op-log";

    /** 数据变动日志. */
    String CHANGE_LOG = PREFIX + "/change-log";

    /** 运行日志. */
    String RUN_LOG = PREFIX + "/run-log";

    /** 异步处理任务. */
    String ASYNC_PROC_TASK = PREFIX + "/async-proc-task";

}
