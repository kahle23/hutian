package hutian.basic.service.impl;

import baibao.db.jdbc.mybatisplus.base.BaseServiceImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import hutian.basic.mapper.RunLogMapper;
import hutian.basic.pojo.entity.RunLog;
import hutian.basic.pojo.param.RunLogAddParam;
import hutian.basic.pojo.param.RunLogEditParam;
import hutian.basic.pojo.query.RunLogQuery;
import hutian.basic.pojo.result.RunLogResult;
import hutian.basic.service.RunLogService;
import kunlun.data.Dict;
import kunlun.data.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.util.StrUtil.isNotBlank;
import static java.util.Objects.nonNull;

/**
 * 运行日志表的服务实现类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@Service
public class RunLogServiceImpl extends BaseServiceImpl<RunLogMapper, RunLog
        , RunLogAddParam, RunLogEditParam, RunLogQuery, RunLogResult> implements RunLogService {

    @Override
    protected RunLog fromAddParam(RunLogAddParam param) {
        // 默认值处理
        if (param.getData() == null) {
            param.setData(Dict.of());
        }
        // 转换成实体
        RunLog entity = BeanUtil.beanToBean(param, RunLog.class);
        entity.setDataJson(JSONUtil.toJsonStr(param.getData()));
        return entity;
    }

    @Override
    protected RunLog fromEditParam(RunLogResult old, RunLogEditParam param) {
        return BeanUtil.beanToBean(param, RunLog.class);
    }

    @Override
    protected MPJLambdaWrapper<RunLog> buildQueryWrapper(RunLogQuery query) {
        return JoinWrappers.lambda(RunLog.class)
                .in(isNotEmpty(query.getIdList()), RunLog::getId, query.getIdList())
                .like(isNotBlank(query.getServerNameLike()), RunLog::getServerName, query.getServerNameLike())
                .like(isNotBlank(query.getServerAppNameLike()), RunLog::getServerAppName, query.getServerAppNameLike())
                .eq(nonNull(query.getTime()), RunLog::getTime, query.getTime())
                .like(isNotBlank(query.getModuleLike()), RunLog::getModule, query.getModuleLike())
                .like(isNotBlank(query.getMessageLike()), RunLog::getMessage, query.getMessageLike())
                .eq(nonNull(query.getSuccess()), RunLog::getSuccess, query.getSuccess())
                .like(isNotBlank(query.getErrorLike()), RunLog::getError, query.getErrorLike())
                // 排序需要增加判断条件 isNull(query.getScrollByAsc())，是为了滚动分页查询而服务的
                .orderByDesc(ObjUtil.isNull(query.getScrollByAsc()), RunLog::getId)
        ;
    }

    @Override
    protected void processData(RunLogQuery qry, List<RunLogResult> data) {
        if (CollUtil.isEmpty(data)) { return; }
        // 数据处理（列表消息截断，详情看 dataJson）
        data.forEach(item -> {
            item.setMessage(StrUtil.sub(item.getMessage(), 0, 300));
        });
    }

    /**
     * 运行日志自身不记录变动日志（避免日志滚日志）.
     */
    @Override
    protected void changeLog(Object bizId, Object oldData, Object newData, Class<?> targetClz, Object... arguments) {
    }

}
