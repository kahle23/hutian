package hutian.cms.util;

import cn.hutool.http.HtmlUtil;

import java.util.regex.Pattern;

import static cn.hutool.core.util.StrUtil.indexOfIgnoreCase;
import static cn.hutool.core.util.StrUtil.isBlank;

/**
 * CMS 正文纯文本处理工具：剥 Markdown / HTML 标记、按关键字截取命中片段.
 * <p>
 * 服务门户全文搜索的列表展示：数据库按原始正文 LIKE 命中，出参再从剥标记后的纯文本里截取关键字前后文，
 * 避免把 Markdown 符号、HTML 标签、图片地址等噪声带进列表。剥标记只求展示干净，不追求 Markdown 语法完备。
 *
 * @author Hutian
 * @since 2026-11-06
 */
public final class CmsTextUtil {

    private CmsTextUtil() {
    }

    /**
     * Markdown 图片 ![alt](url)，保留 alt.
     */
    private static final Pattern MD_IMAGE = Pattern.compile("!\\[([^\\]]*)]\\([^)]*\\)");

    /**
     * Markdown 链接 [text](url)，保留 text.
     */
    private static final Pattern MD_LINK = Pattern.compile("\\[([^\\]]*)]\\([^)]*\\)");

    /**
     * 行首的 Markdown 块级标记：标题 #、引用 >、无序列表 - * +、有序列表 1.
     */
    private static final Pattern MD_LINE_PREFIX = Pattern.compile("(?m)^[ \\t]{0,3}(?:#{1,6}[ \\t]+|>[ \\t]?|[-*+][ \\t]+|\\d+\\.[ \\t]+)");

    /**
     * 代码围栏起始 ```lang、行内代码与强调符号 ` ** __ ~~、表格竖线（单个 * _ 不动：会误伤标识符里的下划线）.
     */
    private static final Pattern MD_INLINE_MARKS = Pattern.compile("```[\\w-]*|`|\\*\\*|__|~~|\\|");

    /**
     * 连续空白折叠为单空格（含 &nbsp; 反转义出的不换行空格 U+00A0，\s 默认不匹配它）.
     */
    private static final Pattern WHITESPACE = Pattern.compile("[\\s\\u00A0]+");

    /**
     * 剥正文标记为纯文本：HTML 标签与实体、Markdown 图片/链接/块级前缀/行内符号，空白折叠.
     *
     * @param content 正文（Markdown / HTML / 纯文本）
     * @return 纯文本（空正文返回空串）
     */
    public static String stripMarkup(String content) {
        if (isBlank(content)) { return ""; }
        String text = HtmlUtil.unescape(HtmlUtil.cleanHtmlTag(content));
        text = MD_IMAGE.matcher(text).replaceAll("$1");
        text = MD_LINK.matcher(text).replaceAll("$1");
        text = MD_LINE_PREFIX.matcher(text).replaceAll("");
        text = MD_INLINE_MARKS.matcher(text).replaceAll("");
        return WHITESPACE.matcher(text).replaceAll(" ").trim();
    }

    /**
     * 命中片段：纯文本里首个命中关键字（忽略大小写，与 MySQL 默认排序规则下 LIKE 的口径一致）前后各 radius 字，
     * 两端被截断处补省略号.
     *
     * @param content 正文（Markdown / HTML / 纯文本）
     * @param keyword 关键字
     * @param radius  命中位置前后各保留的字符数
     * @return 命中片段；正文或关键字为空、或纯文本未命中（如仅命中在被剥掉的标记/地址里）时返回 null
     */
    public static String snippet(String content, String keyword, int radius) {
        if (isBlank(content) || isBlank(keyword)) { return null; }
        String plain = stripMarkup(content);
        int index = indexOfIgnoreCase(plain, keyword);
        if (index < 0) { return null; }
        int start = Math.max(0, index - radius);
        int end = Math.min(plain.length(), index + keyword.length() + radius);
        return (start > 0 ? "…" : "") + plain.substring(start, end) + (end < plain.length() ? "…" : "");
    }

}
