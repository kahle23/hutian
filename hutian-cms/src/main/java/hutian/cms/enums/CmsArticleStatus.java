package hutian.cms.enums;

import kunlun.data.CodeDefinition;

/**
 * 内容状态的枚举.
 *
 * @author Hutian
 * @since 2026-11-06
 */
public enum CmsArticleStatus implements CodeDefinition {
    /**
     * 草稿
     */
    DRAFT(0, "草稿"),
    /**
     * 已发布
     */
    PUBLISHED(1, "已发布"),
    /**
     * 已下线
     */
    OFFLINE(2, "已下线"),
    ;

    private final String description;
    private final Integer code;

    CmsArticleStatus(Integer code, String description) {
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

    public static CmsArticleStatus parse(Object input) {
        if (input == null) { return null; }
        if (input instanceof CmsArticleStatus) {
            return (CmsArticleStatus) input;
        }
        Integer inputInt;
        if (!(input instanceof Integer)) {
            inputInt = Integer.parseInt(String.valueOf(input));
        }
        else { inputInt = (Integer) input; }
        CmsArticleStatus[] values = values();
        for (CmsArticleStatus value : values) {
            if (value.getCode().equals(inputInt)) { return value; }
        }
        return null;
    }

}
