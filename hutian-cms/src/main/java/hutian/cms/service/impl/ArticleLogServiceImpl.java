package hutian.cms.service.impl;

import baibao.db.jdbc.mybatisplus.base.BaseServiceImpl;
import cn.hutool.core.collection.CollUtil;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import hutian.cms.enums.CmsArticleLogAction;
import hutian.cms.mapper.ArticleLogMapper;
import hutian.cms.mapper.SysUserMapper;
import hutian.cms.pojo.entity.ArticleLog;
import hutian.cms.pojo.entity.SysUser;
import hutian.cms.pojo.param.ArticleLogAddParam;
import hutian.cms.pojo.param.ArticleLogEditParam;
import hutian.cms.pojo.query.ArticleLogQuery;
import hutian.cms.pojo.result.ArticleLogResult;
import hutian.cms.service.ArticleLogService;
import kunlun.data.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static kunlun.exception.util.VerifyUtils.notNull;

/**
 * 内容生命周期留痕表的服务实现类（对外只读，写入统一走 writeLog）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Slf4j
@Service
public class ArticleLogServiceImpl extends BaseServiceImpl<ArticleLogMapper, ArticleLog
        , ArticleLogAddParam, ArticleLogEditParam, ArticleLogQuery, ArticleLogResult> implements ArticleLogService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public void writeLog(Long articleId, CmsArticleLogAction action, String detail) {
        // 参数校验
        notNull(articleId, "内容ID不能为空！");
        notNull(action, "动作类型不能为空！");
        // 操作人取当前登录人（定时任务场景无登录上下文/无登录人，为 NULL，与 ArticleServiceImpl.currentUser 同口径）
        Long operatorId = ArticleServiceImpl.currentUser();
        // 写入留痕（主键 AUTO_INCREMENT，审计字段由填充器补齐）
        ArticleLog entity = new ArticleLog();
        entity.setArticleId(articleId);
        entity.setActionType(action.getCode());
        entity.setOperatorId(operatorId);
        entity.setActionTime(new Date());
        entity.setDetail(detail);
        save(entity);
    }

    @Override
    protected ArticleLog fromAddParam(ArticleLogAddParam param) {
        return BeanUtil.beanToBean(param, ArticleLog.class);
    }

    @Override
    protected ArticleLog fromEditParam(ArticleLogResult old, ArticleLogEditParam param) {
        return BeanUtil.beanToBean(param, ArticleLog.class);
    }

    @Override
    protected MPJLambdaWrapper<ArticleLog> buildQueryWrapper(ArticleLogQuery query) {
        return JoinWrappers.lambda(ArticleLog.class)
                .in(isNotEmpty(query.getIdList()), ArticleLog::getId, query.getIdList())
                .eq(nonNull(query.getArticleId()), ArticleLog::getArticleId, query.getArticleId())
                .in(isNotEmpty(query.getActionTypeList()), ArticleLog::getActionType, query.getActionTypeList())
                // 默认排序：动作时间倒序（时间线展示口径）
                .orderByDesc(CollUtil.isEmpty(query.getSortFields()), ArticleLog::getActionTime)
        ;
    }

    /**
     * 统一的留痕表数据处理逻辑：回填操作人姓名（本批批量查，避免 N+1）.
     */
    @Override
    protected void processData(ArticleLogQuery qry, List<ArticleLogResult> data) {
        if (CollUtil.isEmpty(data)) { return; }
        // 操作人ID 提取（定时任务产生的 NULL 操作人跳过）
        List<Long> operatorIds = data.stream().filter(Objects::nonNull)
                .map(ArticleLogResult::getOperatorId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, SysUser> userMap = emptyMap();
        if (isNotEmpty(operatorIds)) {
            List<SysUser> users = sysUserMapper.selectBatchIds(operatorIds);
            if (isNotEmpty(users)) {
                userMap = users.stream().filter(Objects::nonNull)
                        .collect(Collectors.toMap(SysUser::getId, Function.identity(), (k1, k2) -> k1));
            }
        }
        // 姓名回填
        for (ArticleLogResult row : data) {
            if (row == null || isNull(row.getOperatorId())) { continue; }
            SysUser user = userMap.get(row.getOperatorId());
            row.setOperatorName(nonNull(user) ? user.getName() : null);
        }
    }

    /**
     * 留痕表自身不记录变动日志（避免日志滚日志）.
     */
    @Override
    protected void changeLog(Object bizId, Object oldData, Object newData, Class<?> targetClz, Object... arguments) {
    }

}
