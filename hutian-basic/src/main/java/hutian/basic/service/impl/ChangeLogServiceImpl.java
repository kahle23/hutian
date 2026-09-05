package hutian.basic.service.impl;

import baibao.db.jdbc.mybatisplus.base.BaseServiceImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import hutian.basic.mapper.ChangeLogMapper;
import hutian.basic.pojo.entity.ChangeLog;
import hutian.basic.pojo.param.ChangeLogAddParam;
import hutian.basic.pojo.param.ChangeLogEditParam;
import hutian.basic.pojo.query.ChangeLogQuery;
import hutian.basic.pojo.result.ChangeLogResult;
import hutian.basic.service.ChangeLogService;
import kunlun.data.Dict;
import kunlun.data.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.util.StrUtil.isNotBlank;
import static java.util.Objects.nonNull;
import static kunlun.common.constant.Numbers.FIVE_HUNDRED;
import static kunlun.common.constant.Numbers.ZERO;

/**
 * 数据变动日志表的服务实现类.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@Service
public class ChangeLogServiceImpl extends BaseServiceImpl<ChangeLogMapper, ChangeLog
        , ChangeLogAddParam, ChangeLogEditParam, ChangeLogQuery, ChangeLogResult> implements ChangeLogService {

    @Override
    protected ChangeLog fromAddParam(ChangeLogAddParam param) {
        // 默认值处理
        if (param.getData() == null) {
            param.setData(Dict.of());
        }
        // 转换成实体
        ChangeLog entity = BeanUtil.beanToBean(param, ChangeLog.class);
        entity.setDataJson(JSONUtil.toJsonStr(param.getData()));
        return entity;
    }

    @Override
    protected ChangeLog fromEditParam(ChangeLogResult old, ChangeLogEditParam param) {
        return BeanUtil.beanToBean(param, ChangeLog.class);
    }

    @Override
    protected MPJLambdaWrapper<ChangeLog> buildQueryWrapper(ChangeLogQuery query) {
        return JoinWrappers.lambda(ChangeLog.class)
                .in(isNotEmpty(query.getIdList()), ChangeLog::getId, query.getIdList())
                .eq(nonNull(query.getTime()), ChangeLog::getTime, query.getTime())
                .in(isNotEmpty(query.getUserIdList()), ChangeLog::getUserId, query.getUserIdList())
                .eq(isNotBlank(query.getUserType()), ChangeLog::getUserType, query.getUserType())
                .like(isNotBlank(query.getUserDisplayNameLike()), ChangeLog::getUserDisplayName, query.getUserDisplayNameLike())
                .in(isNotEmpty(query.getOrgIdList()), ChangeLog::getOrgId, query.getOrgIdList())
                .like(isNotBlank(query.getOrgNameLike()), ChangeLog::getOrgName, query.getOrgNameLike())
                .eq(isNotBlank(query.getPlatform()), ChangeLog::getPlatform, query.getPlatform())
                .in(isNotEmpty(query.getTenantIdList()), ChangeLog::getTenantId, query.getTenantIdList())
                .in(isNotEmpty(query.getBusinessIdList()), ChangeLog::getBusinessId, query.getBusinessIdList())
                .eq(nonNull(query.getBusinessType()), ChangeLog::getBusinessType, query.getBusinessType())
                .like(isNotBlank(query.getMessageLike()), ChangeLog::getMessage, query.getMessageLike())
                .like(isNotBlank(query.getServerNameLike()), ChangeLog::getServerName, query.getServerNameLike())
                .like(isNotBlank(query.getServerAppNameLike()), ChangeLog::getServerAppName, query.getServerAppNameLike())
                // 排序需要增加判断条件 isNull(query.getScrollByAsc())，是为了滚动分页查询而服务的
                .orderByDesc(ObjUtil.isNull(query.getScrollByAsc()), ChangeLog::getId)
        ;
    }

    @Override
    protected void processData(ChangeLogQuery qry, List<ChangeLogResult> data) {
        if (CollUtil.isEmpty(data)) { return; }
        // 数据处理（列表消息截断，详情看 dataJson）
        data.forEach(item -> {
            if (StrUtil.isNotBlank(item.getMessage())) {
                item.setMessage(StrUtil.sub(item.getMessage(), ZERO, FIVE_HUNDRED));
            }
        });
    }

    /**
     * 变动日志自身不记录变动日志（避免日志滚日志）.
     */
    @Override
    protected void changeLog(Object bizId, Object oldData, Object newData, Class<?> targetClz, Object... arguments) {
    }

}
