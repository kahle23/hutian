package hutian.cms.controller;

import hutian.cms.constant.UrlConstant;
import hutian.cms.pojo.query.PortalQuery;
import hutian.cms.pojo.result.CategoryPortalListResult;
import hutian.cms.pojo.result.PortalResult;
import hutian.cms.service.ArticlePortalService;
import kunlun.common.Page;
import kunlun.common.Result;
import kunlun.core.annotation.OperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 内容用户端（门户）的前端控制器：登录即可访问，仅已发布且有效期内.
 *
 * @folder 内容管理/公告中心
 * @author Hutian
 * @since 2026-11-06
 */
@Slf4j
@RestController
@RequestMapping(UrlConstant.ARTICLE_PORTAL)
public class ArticlePortalController {

    @Resource
    private ArticlePortalService articlePortalService;

    /**
     * 公告-用户端分页（仅已发布且有效期内；行含 isRead/snippet/categoryName/publishUserName）
     *
     * @param query 查询条件（categoryId + typeList + keyword；fullText=true 时 keyword 全文匹配并回填 snippet）
     * @return 公告分页
     */
    @OperationLog(name = "公告-用户端分页")
    @PostMapping("/query-page")
    public Result<Page<PortalResult>> queryPage(@RequestBody @Validated PortalQuery query) {

        return Result.success(articlePortalService.queryPage(query));
    }

    /**
     * 公告-用户端详情（打开即浏览数 +1 并记已读）
     *
     * @param recordId 公告ID
     * @return 公告详情
     */
    @OperationLog(name = "公告-用户端详情")
    @GetMapping("/{recordId}")
    public Result<PortalResult> detail(@PathVariable("recordId") Long recordId) {

        return Result.success(articlePortalService.detail(recordId));
    }

    /**
     * 公告-最新公告（type=12，按发布时间倒序前 limit 条）
     *
     * @param limit 条数（默认 5）
     * @return 最新公告列表
     */
    @OperationLog(name = "公告-最新公告")
    @GetMapping("/notice-latest")
    public Result<List<PortalResult>> noticeLatest(@RequestParam(value = "limit", required = false) Integer limit) {

        return Result.success(articlePortalService.noticeLatest(limit));
    }

    /**
     * 公告-当前用户未读数（红点/角标，公告口径：内容类型=12）
     *
     * @return 未读数
     */
    @OperationLog(name = "公告-未读数")
    @GetMapping("/unread-count")
    public Result<Number> unreadCount() {

        return Result.success(articlePortalService.unreadCount());
    }

    /**
     * 公告-分类文件夹列表（启用分类 GROUP BY 可见内容计数 + 总数 + 未分类数）
     *
     * @param type 内容类型（必填）
     * @return 分类文件夹列表
     */
    @OperationLog(name = "公告-分类文件夹列表")
    @GetMapping("/category-list")
    public Result<CategoryPortalListResult> categoryList(@RequestParam("type") Integer type) {

        return Result.success(articlePortalService.categoryList(type));
    }

    /**
     * 公告-我的已读列表（已读表 JOIN，readTime=首次阅读时间倒序）
     *
     * @param query 查询条件
     * @return 我的已读分页
     */
    @OperationLog(name = "公告-我的已读")
    @PostMapping("/my-read")
    public Result<Page<PortalResult>> myRead(@RequestBody @Validated PortalQuery query) {

        return Result.success(articlePortalService.myRead(query));
    }

    /**
     * 公告-我的未读列表（可见内容过滤未读）
     *
     * @param query 查询条件
     * @return 我的未读分页
     */
    @OperationLog(name = "公告-我的未读")
    @PostMapping("/my-unread")
    public Result<Page<PortalResult>> myUnread(@RequestBody @Validated PortalQuery query) {

        return Result.success(articlePortalService.myUnread(query));
    }

}
