package hutian.basic.service.impl;

import baibao.db.jdbc.mybatisplus.base.BaseServiceImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import hutian.basic.mapper.OpLogMapper;
import hutian.basic.pojo.entity.OpLog;
import hutian.basic.pojo.param.OpLogAddParam;
import hutian.basic.pojo.param.OpLogEditParam;
import hutian.basic.pojo.query.OpLogQuery;
import hutian.basic.pojo.result.OpLogResult;
import hutian.basic.service.OpLogService;
import kunlun.data.Dict;
import kunlun.data.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.util.StrUtil.isNotBlank;
import static java.util.Objects.nonNull;

/**
 * 操作日志表的服务实现类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@Service
public class OpLogServiceImpl extends BaseServiceImpl<OpLogMapper, OpLog
        , OpLogAddParam, OpLogEditParam, OpLogQuery, OpLogResult> implements OpLogService {

    @Override
    protected OpLog fromAddParam(OpLogAddParam param) {
        // 默认值处理
        if (param.getData() == null) {
            param.setData(Dict.of());
        }
        // 转换成实体
        OpLog entity = BeanUtil.beanToBean(param, OpLog.class);
        entity.setDataJson(JSONUtil.toJsonStr(param.getData()));
        return entity;
    }

    @Override
    protected OpLog fromEditParam(OpLogResult old, OpLogEditParam param) {
        return BeanUtil.beanToBean(param, OpLog.class);
    }

    @Override
    protected MPJLambdaWrapper<OpLog> buildQueryWrapper(OpLogQuery query) {
        return JoinWrappers.lambda(OpLog.class)
                .in(isNotEmpty(query.getIdList()), OpLog::getId, query.getIdList())
                .like(isNotBlank(query.getNameLike()), OpLog::getName, query.getNameLike())
                .eq(nonNull(query.getTime()), OpLog::getTime, query.getTime())
                .eq(isNotBlank(query.getMessage()), OpLog::getMessage, query.getMessage())
                .in(isNotEmpty(query.getUserIdList()), OpLog::getUserId, query.getUserIdList())
                .eq(isNotBlank(query.getUserType()), OpLog::getUserType, query.getUserType())
                .like(isNotBlank(query.getUserDisplayNameLike()), OpLog::getUserDisplayName, query.getUserDisplayNameLike())
                .in(isNotEmpty(query.getOrgIdList()), OpLog::getOrgId, query.getOrgIdList())
                .like(isNotBlank(query.getOrgNameLike()), OpLog::getOrgName, query.getOrgNameLike())
                .eq(isNotBlank(query.getPlatform()), OpLog::getPlatform, query.getPlatform())
                .in(isNotEmpty(query.getTenantIdList()), OpLog::getTenantId, query.getTenantIdList())
                .in(isNotEmpty(query.getTraceIdList()), OpLog::getTraceId, query.getTraceIdList())
                .eq(isNotBlank(query.getRequestApiUri()), OpLog::getRequestApiUri, query.getRequestApiUri())
                .eq(isNotBlank(query.getRequestMethod()), OpLog::getRequestMethod, query.getRequestMethod())
                .eq(nonNull(query.getSuccess()), OpLog::getSuccess, query.getSuccess())
                .like(isNotBlank(query.getErrorLike()), OpLog::getError, query.getErrorLike())
                .eq(nonNull(query.getTimeSpent()), OpLog::getTimeSpent, query.getTimeSpent())
                .in(isNotEmpty(query.getClientAppIdList()), OpLog::getClientAppId, query.getClientAppIdList())
                .in(isNotEmpty(query.getClientDeviceIdList()), OpLog::getClientDeviceId, query.getClientDeviceIdList())
                .like(isNotBlank(query.getClientUserAgentLike()), OpLog::getClientUserAgent, query.getClientUserAgentLike())
                .eq(isNotBlank(query.getClientNetAddress()), OpLog::getClientNetAddress, query.getClientNetAddress())
                .eq(isNotBlank(query.getClientGeoAddress()), OpLog::getClientGeoAddress, query.getClientGeoAddress())
                .like(isNotBlank(query.getServerNameLike()), OpLog::getServerName, query.getServerNameLike())
                .like(isNotBlank(query.getServerAppNameLike()), OpLog::getServerAppName, query.getServerAppNameLike())
                // 排序需要增加判断条件 isNull(query.getScrollByAsc())，是为了滚动分页查询而服务的
                .orderByDesc(ObjUtil.isNull(query.getScrollByAsc()), OpLog::getId)
        ;
    }

    @Override
    protected void processData(OpLogQuery qry, List<OpLogResult> data) {
        if (CollUtil.isEmpty(data)) { return; }
        // 数据处理
        data.forEach(item -> {
        });
    }

    /**
     * 操作日志自身不记录变动日志（避免日志滚日志）.
     */
    @Override
    protected void changeLog(Object bizId, Object oldData, Object newData, Class<?> targetClz, Object... arguments) {
    }

}
