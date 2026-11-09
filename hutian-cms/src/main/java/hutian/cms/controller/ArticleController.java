package hutian.cms.controller;

import baibao.common.dto.LongIdsDTO;
import hutian.cms.constant.UrlConstant;
import hutian.cms.pojo.param.ArticleAddParam;
import hutian.cms.pojo.param.ArticleEditParam;
import hutian.cms.pojo.param.ArticlePublishParam;
import hutian.cms.pojo.param.ArticleTopParam;
import hutian.cms.pojo.query.ArticleQuery;
import hutian.cms.pojo.query.ArticleReadQuery;
import hutian.cms.pojo.result.ArticleResult;
import hutian.cms.pojo.result.ArticleSmpResult;
import hutian.cms.pojo.result.ReadStatResult;
import hutian.cms.pojo.result.ReadUserResult;
import hutian.cms.service.ArticleService;
import kunlun.common.Page;
import kunlun.common.Result;
import kunlun.core.annotation.OperationLog;
import kunlun.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 内容的前端控制器（管理端：文章/公告/版本/帮助/提示共用）.
 *
 * @folder 内容管理/内容
 * @author Hutian
 * @since 2026-11-06
 */
@Slf4j
@RestController
@RequestMapping(UrlConstant.ARTICLE)
public class ArticleController {

    @Resource
    private ArticleService articleService;

    /**
     * 内容-新增（status=1 直接发布；publishTime 为未来时间=定时发布，保持草稿）
     *
     * @param param 待增加的数据
     * @return 增加的记录的ID
     */
    @OperationLog(name = "内容-新增")
    @PostMapping("/add")
    public Result<Number> add(@RequestBody @Validated ArticleAddParam param) {

        return Result.success(articleService.addRecord(param));
    }

    /**
     * 内容-编辑（不含 status/publishTime，发布走 /publish 专门接口）
     *
     * @param param 待编辑的数据
     * @return 操作结果
     */
    @OperationLog(name = "内容-编辑")
    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody @Validated ArticleEditParam param) {
        articleService.editRecord(param);
        return Result.success();
    }

    /**
     * 内容-删除（批量逻辑删）
     *
     * @param param 待删除的数据
     * @return 操作结果
     */
    @OperationLog(name = "内容-删除")
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody @Validated LongIdsDTO param) {
        articleService.deleteByLongIds(param.getIds());
        return Result.success();
    }

    /**
     * 内容-详情（全字段 + 封面图/附件反序列化 + 分类名/发布人姓名 + 生命周期留痕）
     *
     * @param recordId 内容的ID
     * @return 查询到的内容详情
     */
    @OperationLog(name = "内容-详情")
    @GetMapping("/{recordId}")
    public Result<ArticleResult> detail(@PathVariable("recordId") Long recordId) {

        return Result.success(articleService.getById2(recordId));
    }

    /**
     * 内容-分页列表（默认排序：置顶权重倒序 → 发布时间倒序 → id 倒序）
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "内容-分页列表")
    @PostMapping("/query-page")
    public Result<Page<ArticleSmpResult>> queryPage(@RequestBody @Validated ArticleQuery query) {
        Page<ArticleResult> page = articleService.queryPage(query);
        return Result.success(PageUtil.handleResult(page, ArticleSmpResult.class));
    }

    /**
     * 内容-列表（同分页条件不分页）
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @OperationLog(name = "内容-列表")
    @PostMapping("/query-list")
    public Result<List<ArticleSmpResult>> queryList(@RequestBody @Validated ArticleQuery query) {
        List<ArticleResult> list = articleService.queryList(query);
        return Result.success(PageUtil.handleResult(list, ArticleSmpResult.class).getData());
    }

    /**
     * 内容-发布（立即发布/定时发布/重新发布三合一；已发布拦截报错）
     *
     * @param param 发布入参（ids + publishTime? + expireTime?）
     * @return 操作结果
     */
    @OperationLog(name = "内容-发布")
    @PostMapping("/publish")
    public Result<Void> publish(@RequestBody @Validated ArticlePublishParam param) {
        articleService.publish(param);
        return Result.success();
    }

    /**
     * 内容-下线（仅已发布可下线）
     *
     * @param param 待下线的数据
     * @return 操作结果
     */
    @OperationLog(name = "内容-下线")
    @PostMapping("/offline")
    public Result<Void> offline(@RequestBody @Validated LongIdsDTO param) {
        articleService.offline(param.getIds());
        return Result.success();
    }

    /**
     * 内容-置顶/取消置顶（topWeight 0=取消置顶，大于 0 置顶）
     *
     * @param param 置顶入参（id + topWeight）
     * @return 操作结果
     */
    @OperationLog(name = "内容-置顶")
    @PostMapping("/top")
    public Result<Void> top(@RequestBody @Validated ArticleTopParam param) {
        articleService.top(param);
        return Result.success();
    }

    /**
     * 内容-复制为草稿（标题+"（副本）"，清置顶/发布/到期/浏览数）
     *
     * @param recordId 源内容ID
     * @return 新内容的ID
     */
    @OperationLog(name = "内容-复制")
    @PostMapping("/copy/{recordId}")
    public Result<Number> copy(@PathVariable("recordId") Long recordId) {

        return Result.success(articleService.copy(recordId));
    }

    /**
     * 内容-触达统计（目标人数=启用用户数/已读/未读/触达率）
     *
     * @param recordId 内容的ID
     * @return 触达统计结果
     */
    @OperationLog(name = "内容-触达统计")
    @GetMapping("/{recordId}/read-stat")
    public Result<ReadStatResult> readStat(@PathVariable("recordId") Long recordId) {

        return Result.success(articleService.readStat(recordId));
    }

    /**
     * 内容-已读名单（分页）
     *
     * @param recordId 内容的ID
     * @param query    分页入参
     * @return 已读名单分页
     */
    @OperationLog(name = "内容-已读名单")
    @PostMapping("/{recordId}/read-users")
    public Result<Page<ReadUserResult>> readUsers(@PathVariable("recordId") Long recordId
            , @RequestBody @Validated ArticleReadQuery query) {

        return Result.success(articleService.readUsers(recordId, query));
    }

    /**
     * 内容-未读名单（启用用户 LEFT JOIN 已读表 IS NULL，分页）
     *
     * @param recordId 内容的ID
     * @param query    分页入参
     * @return 未读名单分页
     */
    @OperationLog(name = "内容-未读名单")
    @PostMapping("/{recordId}/unread-users")
    public Result<Page<ReadUserResult>> unreadUsers(@PathVariable("recordId") Long recordId
            , @RequestBody @Validated ArticleReadQuery query) {

        return Result.success(articleService.unreadUsers(recordId, query));
    }

}
