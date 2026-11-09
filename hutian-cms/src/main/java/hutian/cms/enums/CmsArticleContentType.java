package hutian.cms.enums;

import kunlun.data.CodeDefinition;

/**
 * 内容正文格式的枚举.
 *
 * @author Hutian
 * @since 2026-11-06
 */
public enum CmsArticleContentType implements CodeDefinition {
    /**
     * 缺省
     */
    DEFAULT(0, "缺省"),
    /**
     * 纯文本
     */
    PLAIN_TEXT(1, "纯文本"),
    /**
     * Markdown
     */
    MARKDOWN(2, "Markdown"),
    /**
     * Html
     */
    HTML(3, "Html"),
    ;

    private final String description;
    private final Integer code;

    CmsArticleContentType(Integer code, String description) {
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

    public static CmsArticleContentType parse(Object input) {
        if (input == null) { return null; }
        if (input instanceof CmsArticleContentType) {
            return (CmsArticleContentType) input;
        }
        Integer inputInt;
        if (!(input instanceof Integer)) {
            inputInt = Integer.parseInt(String.valueOf(input));
        }
        else { inputInt = (Integer) input; }
        CmsArticleContentType[] values = values();
        for (CmsArticleContentType value : values) {
            if (value.getCode().equals(inputInt)) { return value; }
        }
        return DEFAULT;
    }

}
