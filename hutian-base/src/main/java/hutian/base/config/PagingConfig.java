package hutian.base.config;

import baibao.data.paging.support.PageHelperPagingProcessor;
import baibao.data.paging.support.PageHelperResultProcessor;
import kunlun.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * 分页基建注册.
 * <p>
 * baibao 的 BaseService 分页查询走 kunlun 的 PageUtil，
 * 需要注册 PageHelper 实现的处理器（PageHelper 基于 ThreadLocal 拦截 SQL），
 * 不注册时 PageUtil.startPage 会抛 UnsupportedOperationException.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Slf4j
@Configuration
public class PagingConfig implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        PageUtil.setPagingProcessor(new PageHelperPagingProcessor());
        PageUtil.setPageResultProcessor(new PageHelperResultProcessor());
        log.info("The paging processors (PageHelper) are registered successfully. ");
    }

}
