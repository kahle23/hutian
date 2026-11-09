package hutian.cms.enums;

import kunlun.data.CodeDefinition;

/**
 * 内容类型的枚举（cms_category.type 与 cms_article.type 同口径）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
public enum CmsArticleType implements CodeDefinition {
    /**
     * 缺省
     */
    DEFAULT(0, "缺省"),
    /**
     * 文章
     */
    ARTICLE(1, "文章"),
    /**
     * 版本
     */
    CHANGELOG(11, "版本"),
    /**
     * 公告
     */
    NOTICE(12, "公告"),
    /**
     * 帮助
     */
    HELP(13, "帮助"),
    /**
     * 提示
     */
    TIPS(14, "提示"),
    ;

    private final String description;
    private final Integer code;

    CmsArticleType(Integer code, String description) {
        this.description = description;
        this.code = code;
    }

    @Override
    public Integer getCode() {

        return code;
    }

    @Override
    public String getDescription() {

        return description;
    }

    /**
     * 判断码值是否为已注册的内容类型（含缺省 0）.
     *
     * @param code 待判断的码值
     * @return 已注册返回 true
     */
    public static boolean contains(Integer code) {
        if (code == null) { return false; }
        CmsArticleType[] values = values();
        for (CmsArticleType value : values) {
            if (value.getCode().equals(code)) { return true; }
        }
        return false;
    }

    public static CmsArticleType parse(Object input) {
        if (input == null) { return null; }
        if (input instanceof CmsArticleType) {
            return (CmsArticleType) input;
        }
        Integer inputInt;
        if (!(input instanceof Integer)) {
            inputInt = Integer.parseInt(String.valueOf(input));
        }
        else { inputInt = (Integer) input; }
        CmsArticleType[] values = values();
        for (CmsArticleType value : values) {
            if (value.getCode().equals(inputInt)) { return value; }
        }
        return DEFAULT;
    }

}
