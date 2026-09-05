package hutian.basic.service.impl;

import baibao.common.enums.QueryMode;
import baibao.db.jdbc.mybatisplus.base.BaseServiceImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import hutian.basic.mapper.AsyncProcTaskMapper;
import hutian.basic.pojo.entity.AsyncProcTask;
import hutian.basic.pojo.param.AsyncProcTaskAddParam;
import hutian.basic.pojo.param.AsyncProcTaskEditParam;
import hutian.basic.pojo.query.AsyncProcTaskQuery;
import hutian.basic.pojo.result.AsyncProcTaskResult;
import hutian.basic.service.AsyncProcTaskService;
import kunlun.data.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.util.StrUtil.isNotBlank;
import static java.util.Objects.nonNull;

/**
 * 异步处理的任务表的服务实现类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@Service
public class AsyncProcTaskServiceImpl extends BaseServiceImpl<AsyncProcTaskMapper, AsyncProcTask
        , AsyncProcTaskAddParam, AsyncProcTaskEditParam, AsyncProcTaskQuery, AsyncProcTaskResult> implements AsyncProcTaskService {

    @Override
    protected AsyncProcTask fromAddParam(AsyncProcTaskAddParam param) {
        return BeanUtil.beanToBean(param, AsyncProcTask.class);
    }

    @Override
    protected AsyncProcTask fromEditParam(AsyncProcTaskResult old, AsyncProcTaskEditParam param) {
        return BeanUtil.beanToBean(param, AsyncProcTask.class);
    }

    @Override
    protected MPJLambdaWrapper<AsyncProcTask> buildQueryWrapper(AsyncProcTaskQuery query) {
        return JoinWrappers.lambda(AsyncProcTask.class)
                .in(isNotEmpty(query.getIdList()), AsyncProcTask::getId, query.getIdList())
                .eq(nonNull(query.getBusinessType()), AsyncProcTask::getBusinessType, query.getBusinessType())
                .in(isNotEmpty(query.getBusinessIdList()), AsyncProcTask::getBusinessId, query.getBusinessIdList())
                .eq(nonNull(query.getTaskType()), AsyncProcTask::getTaskType, query.getTaskType())
                .eq(isNotBlank(query.getTaskParameters()), AsyncProcTask::getTaskParameters, query.getTaskParameters())
                .eq(nonNull(query.getBeginTime()), AsyncProcTask::getBeginTime, query.getBeginTime())
                .eq(nonNull(query.getEndTime()), AsyncProcTask::getEndTime, query.getEndTime())
                .eq(isNotBlank(query.getOriginalFiles()), AsyncProcTask::getOriginalFiles, query.getOriginalFiles())
                .like(isNotBlank(query.getOriginalFilenameLike()), AsyncProcTask::getOriginalFilename, query.getOriginalFilenameLike())
                .in(isNotEmpty(query.getStatusList()), AsyncProcTask::getStatus, query.getStatusList())
                .like(isNotBlank(query.getExecutorNameLike()), AsyncProcTask::getExecutorName, query.getExecutorNameLike())
                .eq(isNotBlank(query.getPlatform()), AsyncProcTask::getPlatform, query.getPlatform())
                .in(isNotEmpty(query.getTenantIdList()), AsyncProcTask::getTenantId, query.getTenantIdList())
                .in(isNotEmpty(query.getOwnerIdList()), AsyncProcTask::getOwnerId, query.getOwnerIdList())
                .in(isNotEmpty(query.getOwnOrgIdList()), AsyncProcTask::getOwnOrgId, query.getOwnOrgIdList())
                // 排序需要增加判断条件 isNull(query.getScrollByAsc())，是为了滚动分页查询而服务的
                .orderByDesc(ObjUtil.isNull(query.getScrollByAsc()), AsyncProcTask::getId)
        ;
    }

    @Override
    protected void processData(AsyncProcTaskQuery qry, List<AsyncProcTaskResult> data) {
        if (CollUtil.isEmpty(data)) { return; }
        // 判断查询模式
        List<QueryMode> allow = Arrays.asList(QueryMode.FULL, QueryMode.ONLY_PROCESS);
        if (nonNull(qry) && nonNull(qry.getQueryMode()) && !allow.contains(qry.getQueryMode())) { return; }
        // 数据处理
        data.forEach(item -> {
        });
    }

    /**
     * 异步任务自身不记录变动日志（避免日志滚日志）.
     */
    @Override
    protected void changeLog(Object bizId, Object oldData, Object newData, Class<?> targetClz, Object... arguments) {
    }

}
