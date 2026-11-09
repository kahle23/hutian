# 壶天 hutian

> 壶里乾坤大，炉中日月长。

壶中日月，炼器成天 —— 装得下多方天地的应用平台，也是组件淬炼与诞生之地。

技术栈：Java 8 + Spring Boot 2.3.2 + MyBatis-Plus(-Join) + baibao/kunlun 的多模块工程，首个业务域为健康档案 / 账务 / 博客 / 知识库 / 图库。

Java 8 + Spring Boot 2.3.2 + MyBatis-Plus(-Join) + baibao/kunlun 的多模块样板工程。
运行时外部依赖只有 **MySQL**（不引入 Redis）。

## 模块结构

| 模块 | 说明 |
|---|---|
| hutian-common | 公共依赖收敛（哑 jar）：统一技术栈与版本，业务组件依赖它获得全部技术栈；分页/审计填充/令牌管理器等平台基建由 hutian-boot 装配 |
| hutian-basic | 基础服务域：操作日志 / 数据变动日志 / 运行日志 / 异步处理任务，URL 前缀 `/basic`；自带 spring.factories，可作为 jar 供外部工程引入 |
| hutian-cms | 内容服务域：文章（草稿-发布-定时发布-下线-置顶-复制）/ 分类 / 生命周期留痕 / 门户（公告中心、已读未读、触达统计、全文搜索）+ 到点发布/到期下线定时任务，URL 前缀 `/cms`；自带 spring.factories，契约唯一事实源见 `docs/cms-design.md` |
| hutian-boot | 应用装配：启动类 / 登录 / 跨域 / 健康检查，唯一可执行模块，产出 fat jar；前端产物复制进 static |
| sql | 数据库初始化脚本（init.sql 建库建 sys_user，basic.sql 建 base_op_log 等 4 张基础服务表） |

新业务域（健康/账务/博客/知识库/图库）= 新增 `hutian-xxx` 模块，加入根 pom 的 modules，启动类 `@MapperScan("hutian.*.mapper")` 自动覆盖。根 pom 只做聚合不做 parent：新模块的 pom 以 `baibao-spring-boot-standard-parent` 为 parent，并在 properties 里声明 `baibao.version`（父 pom 的 baibao 是实际依赖，不声明会被 nearest-wins 静默降级到父 pom 默认版本）。

## 构建

### 打包 fat jar

唯一可执行模块是 `hutian-boot`：spring-boot-maven-plugin repackage 把全部依赖
（含各业务域模块）与前端静态资源并入一个 jar，产物固定为 `hutian-boot/target/hutian-boot.jar`。

```bash
# 标准打包：使用云效私服配置（baibao 在私服/阿里云镜像上）
mvn -s E:\Repository\Maven\_yunxiao_settings.xml clean package

# 切 test 环境打包（默认 dev；profile 经 maven filtering 填充 application.yml 的 @activatedProfile@ / @applicationVersion@）
mvn -s E:\Repository\Maven\_yunxiao_settings.xml clean package -P test

# 只打后端：跳过 npm install/build，复用 ../hutian-web 已有 dist 产物
mvn -s E:\Repository\Maven\_yunxiao_settings.xml clean package -DskipFrontendBuild=true
```

前端联动（hutian-boot 内置 `frontend` profile，满足条件自动激活，无需 `-P` 指定）：

- 同级目录存在 `hutian-web/package.json` 时激活：打包时自动执行 `npm install` + `npm run build`，并把 dist 复制进 jar 的 `static/`；前端仓库不在场则自动跳过
- Windows 下自动改用 `npm.cmd`（`os-windows` profile）
- 因此发布态无需手工 `npm run build`，直接重新打包，前端产物即自动并入 fat jar

运行：

```bash
java -jar hutian-boot/target/hutian-boot.jar

# jar 已开 <executable>true</executable>（前置启动脚本），Linux/macOS 上也可直接执行
./hutian-boot/target/hutian-boot.jar
```

部署仅需一个 fat jar + JDK 8 + MySQL（JDK 8 启动原因见文末注意事项）。

### 前端联调

前端工程为同级的独立仓库 `hutian-web`（Vue3 + Element Plus），
开发态 `npm run dev` 直连本服务（http://localhost:1210，跨域已放开），
发布态重新打包即自动并入 fat jar（见上）。

## 骨架现状与待办

- [x] 多模块骨架 / 健康检查 `GET /base/ping` / 登录 `POST /system/login`
- [x] 基础服务域 hutian-basic：操作/变动/运行日志与异步任务的增删改查、分页、滚动分页接口（`/basic/op-log` 等，已实跑验证）
- [x] 内容服务域 hutian-cms：文章/分类管理端 + 公告中心门户全流程（`/cms/article` 等 29 接口，接口轮 38 PASS + E2E 轮 4 PASS，契约见 `docs/cms-design.md`、交付说明见 `docs/cms-delivery.md`）
- [x] 真实鉴权（hutian-system 系统域：账号密码登录/登出/当前用户，种子账号 admin/admin123；RBAC 用户/角色/菜单授权，kunlun 安全上下文由 SystemUserService 真实提供；JWT 无状态 token 30 天，资源访问拦截已启用，免登录口仅 /system/login 与 /base/ping）
- [ ] 业务域模块：健康档案 / 账务 / 博客 / 知识库 / 图库

> 运行注意：工程为 Java 8 + Spring Boot 2.3.2，请用 JDK 8 启动（高版本 JDK 上 cglib 代理会失败）。
