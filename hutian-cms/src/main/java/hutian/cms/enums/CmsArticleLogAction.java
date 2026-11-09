package hutian.cms.enums;

import kunlun.data.CodeDefinition;

/**
 * 内容生命周期动作的枚举（cms_article_log.action_type）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
public enum CmsArticleLogAction implements CodeDefinition {
    /**
     * 创建
     */
    CREATE(1, "创建"),
    /**
     * 发布
     */
    PUBLISH(2, "发布"),
    /**
     * 下线
     */
    OFFLINE(3, "下线"),
    /**
     * 置顶
     */
    TOP(4, "置顶"),
    /**
     * 取消置顶
     */
    CANCEL_TOP(5, "取消置顶"),
    /**
     * 编辑
     */
    EDIT(6, "编辑"),
    /**
     * 复制
     */
    COPY(7, "复制"),
    ;

    private final String description;
    private final Integer code;

    CmsArticleLogAction(Integer code, String description) {
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

    public static CmsArticleLogAction parse(Object input) {
        if (input == null) { return null; }
        if (input instanceof CmsArticleLogAction) {
            return (CmsArticleLogAction) input;
        }
        Integer inputInt;
        if (!(input instanceof Integer)) {
            inputInt = Integer.parseInt(String.valueOf(input));
        }
        else { inputInt = (Integer) input; }
        CmsArticleLogAction[] values = values();
        for (CmsArticleLogAction value : values) {
            if (value.getCode().equals(inputInt)) { return value; }
        }
        return null;
    }

}
