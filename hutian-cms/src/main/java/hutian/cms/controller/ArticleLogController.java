package hutian.cms.controller;

import hutian.cms.constant.UrlConstant;
import hutian.cms.pojo.query.ArticleLogQuery;
import hutian.cms.pojo.result.ArticleLogResult;
import hutian.cms.service.ArticleLogService;
import kunlun.common.Page;
import kunlun.common.Result;
import kunlun.core.annotation.OperationLog;
import kunlun.exception.util.VerifyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 内容生命周期留痕的前端控制器（只读：query-page + detail）.
 *
 * @folder 内容管理/留痕
 * @author Hutian
 * @since 2026-11-06
 */
@Slf4j
@RestController
@RequestMapping(UrlConstant.ARTICLE_LOG)
public class ArticleLogController {

    @Resource
    private ArticleLogService articleLogService;

    /**
     * 留痕-分页列表（actionTime 倒序，operatorName 已填）
     *
     * @param query 查询条件（articleId 必填 + actionTypeList 可选）
     * @return 查询结果
     */
    @OperationLog(name = "留痕-分页列表")
    @PostMapping("/query-page")
    public Result<Page<ArticleLogResult>> queryPage(@RequestBody @Validated ArticleLogQuery query) {
        // articleId 必填（getById2 详情链路仅按 idList 查询，故不能放到 Query 的 JSR-303 校验里）
        VerifyUtils.notNull(query.getArticleId(), "文章ID不能为空！");
        return Result.success(articleLogService.queryPage(query));
    }

    /**
     * 留痕-详情
     *
     * @param recordId 留痕的ID
     * @return 查询到的留痕详情
     */
    @OperationLog(name = "留痕-详情")
    @GetMapping("/{recordId}")
    public Result<ArticleLogResult> detail(@PathVariable("recordId") Long recordId) {

        return Result.success(articleLogService.getById2(recordId));
    }

}
