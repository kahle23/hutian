# hutian-system

洞天系统域模块（用户/角色/菜单/权限/登录），仿 `hutian-basic` 的五件套分层与代码风格重写，
参考 `th-core-server` 的 `taihong.coreserv.system` 包主干，剥离机构/岗位/数据权限/验证码/Redis/
临时用户/二维码等历史包袱，是干净版的 RBAC 系统域。

## 模块定位

- 提供用户/角色/菜单的增删改查、分页、树查询
- 提供用户-角色、角色-菜单的全量覆盖式授权
- 提供真实账号密码登录/登出/当前用户信息/当前用户菜单树/当前用户权限列表
- 实现 kunlun 安全上下文 `UserService`（RBAC join SQL），让 kunlun 权限校验真正生效

## 表清单（5 张，DDL + 种子在 `sql/system.sql`）

| 表 | 说明 |
| --- | --- |
| `sys_user` | 用户：account(唯一)/password/name/avatar/enabled + 审计 |
| `sys_role` | 角色：name/code/description/enabled + 审计 |
| `sys_menu` | 菜单兼权限点：parent_id/name/type(1目录/2菜单/3按钮)/permission/sort/enabled/show_flag + 审计 |
| `sys_user_role` | 用户-角色绑定 + 审计 |
| `sys_role_menu` | 角色-菜单绑定 + 审计 |

审计字段统一口径：`platform/tenant_id/create_user/create_time/modify_user/modify_time/delete_status`，
全部 NOT NULL + DEFAULT，软删（delete_status=0 未删 / 1 已删）。

## 接口前缀清单（登记在本模块 `hutian.system.constant.UrlConstant`）

| 前缀 | 控制器 | 说明 |
| --- | --- | --- |
| `/system/login`、`/system/logout`、`/system/current/**` | `LoginController` | 登录/登出/当前会话 |
| `/system/user/**` | `UserController` | 用户增删改查 + 密码管理 + 角色授权 |
| `/system/role/**` | `RoleController` | 角色增删改查 + 菜单授权 |
| `/system/menu/**` | `MenuController` | 菜单增删改查 + 树查询 |

## 种子账号

- 账号：`admin`
- 密码：`admin123`
- 已绑定超级管理员角色（`super_admin`），拥有全部菜单权限

密码摘要用 kunlun `CryptoUtil.digestToHex` 算法（默认 CustomDigester，非明文 SHA-256），
登录校验、重置密码、修改密码一律走同一算法。

## SQL 执行顺序

1. `sql/init.sql` — 建库（其中骨架版 sys_user 已被 system.sql 取代，仅占位）
2. `sql/system.sql` — 系统域 5 张表 DDL + 种子数据（幂等可重复执行）
3. `sql/basic.sql` — basic 域建表

## 构建

```bash
mvn -s "E:/Repository/Maven/_yunxiao_settings.xml" compile
```

裸 `mvn compile` 在本机会因 localRepository 缺 baibao 1.0.27 + JDK 证书过旧失败，
须带 `-s` 指定云效 settings（aliyun 镜像，依赖已缓存），属环境问题非代码问题。
