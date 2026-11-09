package hutian.cms.constant;

/**
 * URL 常量.
 * <p>
 * 与前端 api 层的路径保持一致，cms 域新增 URL 统一在此登记.
 *
 * @author Hutian
 * @since 2026-11-06
 */
public interface UrlConstant {

    /** 模块统一前缀 - cms（内容服务域：内容/公告）. */
    String PREFIX = "/cms";

    /** 内容（文章/公告等，管理端）. */
    String ARTICLE = PREFIX + "/article";

    /** 内容分类. */
    String CATEGORY = PREFIX + "/category";

    /** 内容生命周期留痕. */
    String ARTICLE_LOG = PREFIX + "/article/log";

    /** 内容用户端（门户：仅已发布且有效期内，登录即可访问）. */
    String ARTICLE_PORTAL = PREFIX + "/article/portal";

}
