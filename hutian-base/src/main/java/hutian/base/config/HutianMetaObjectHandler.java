package hutian.base.config;

import kunlun.db.jdbc.mybatisplus.AbstractMetaObjectHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * MyBatis-Plus 实体公共字段自动填充.
 * <p>
 * 洞天当前为单用户系统且鉴权是临时实现（无 kunlun 安全上下文），
 * 审计字段统一填缺省值：platform 取配置（缺省 "hutian"）、tenantId="0"、用户/机构=0；
 * 真实鉴权启用后，将各命令改为从 {@link kunlun.security.SecurityUtil} 取登录人即可.
 *
 * @author Hutian
 * @since 2026-08-26
 */
@Component
public class HutianMetaObjectHandler extends AbstractMetaObjectHandler {

    private final String platform;
    private final String tenantId;

    public HutianMetaObjectHandler(
            @Value("${hutian.platform:hutian}") String platform,
            @Value("${hutian.tenant-id:0}") String tenantId) {
        this.platform = platform;
        this.tenantId = tenantId;
        // nowDate / int0 / int1 已由父类 init() 注册
        registerCommand("platformDef", () -> platform);
        registerCommand("tenantIdDef", () -> tenantId);
        // 列名 -> 取值命令
        registerConfig("platform", "platformDef");
        registerConfig("tenantId", "tenantIdDef");
        registerConfig("ownerId", "int0");
        registerConfig("ownOrgId", "int0");
        registerConfig("createUser", "int0");
        registerConfig("createTime", "nowDate");
        registerConfig("modifyUser", "int0");
        registerConfig("modifyTime", "nowDate");
        registerConfig("deleteStatus", "int0");
    }

}
