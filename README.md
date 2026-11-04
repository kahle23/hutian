# 壶天 hutian

> 壶里乾坤大，炉中日月长。

壶中日月，炼器成天 —— 装得下多方天地的应用平台，也是组件淬炼与诞生之地。

技术栈：Java 8 + Spring Boot 2.3.2 + MyBatis-Plus(-Join) + baibao/kunlun 的多模块工程，首个业务域为健康档案 / 账务 / 博客 / 知识库 / 图库。

Java 8 + Spring Boot 2.3.2 + MyBatis-Plus(-Join) + baibao/kunlun 的多模块样板工程。
运行时外部依赖只有 **MySQL**（不引入 Redis）。

## 模块结构

| 模块 | 说明 |
|---|---|
| hutian-starter | 技术底座（纯库，无应用逻辑）：字段自动填充、分页基建、令牌管理器，自带 spring.factories 自动装配（引 jar 即生效）；业务模块依赖它获得全部技术栈 |
| hutian-basic | 基础服务域（自 baibao-basic-service-plugin 迁移改写）：操作日志 / 数据变动日志 / 运行日志 / 异步处理任务，URL 前缀 `/basic`；自带 spring.factories，可作为 jar 供外部工程引入 |
| hutian-boot | 应用装配：启动类 / 登录 / 跨域 / 健康检查，唯一可执行模块，产出 fat jar；前端产物复制进 static |
| sql | 数据库初始化脚本（init.sql 建库建 sys_user，basic.sql 建 base_op_log 等 4 张基础服务表） |

新业务域（健康/账务/博客/知识库/图库）= 新增 `hutian-xxx` 模块，加入根 pom 的 modules，启动类 `@MapperScan("hutian.*.mapper")` 自动覆盖。

## 构建

```bash
# 使用云效私服配置（baibao 在私服/阿里云镜像上）
mvn -s E:\Repository\Maven\_yunxiao_settings.xml clean package

# 运行（前端产物若已在 ../hutian-web/dist，会自动打进 jar）
java -jar hutian-boot/target/hutian-boot.jar
```

前端工程为同级的独立仓库 `hutian-web`（Vue3 + Element Plus），
开发态 `npm run dev` 直连本服务（http://localhost:1210，跨域已放开），
发布态 `npm run build` 后重新打包即并入 fat jar。

## 骨架现状与待办

- [x] 多模块骨架 / 健康检查 `GET /base/ping` / 登录 `POST /system/login`
- [x] 基础服务域 hutian-basic：操作/变动/运行日志与异步任务的增删改查、分页、滚动分页接口（`/basic/op-log` 等，已实跑验证）
- [ ] 真实鉴权（当前为临时实现：admin/123456 → 随机 token；kunlun 安全上下文已就绪，UserService 占位待接 sys_user）
- [ ] 业务域模块：健康档案 / 账务 / 博客 / 知识库 / 图库

> 运行注意：工程为 Java 8 + Spring Boot 2.3.2，请用 JDK 8 启动（高版本 JDK 上 cglib 代理会失败）。
