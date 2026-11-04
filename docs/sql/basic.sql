-- ============================================================
-- 洞天(hutian) 基础服务域建表脚本
-- 由 baibao-basic-service-plugin 的 sql/db.sql 迁移改写
-- 在 init.sql（建库）之后执行
-- ============================================================

USE `hutian_test`;

-- ------------------------------------------------------------
-- 操作日志表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `base_op_log` (
    `id`                  BIGINT(20)    NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`                VARCHAR(50)   NOT NULL COMMENT '事件名称',
    `time`                DATETIME      NOT NULL COMMENT '时间',
    `message`             TEXT                   DEFAULT NULL COMMENT '消息',
    `user_id`             VARCHAR(50)   NOT NULL DEFAULT '' COMMENT '用户ID',
    `user_type`           VARCHAR(50)   NOT NULL DEFAULT '' COMMENT '用户类型',
    `user_display_name`   VARCHAR(50)   NOT NULL DEFAULT '' COMMENT '用户的展示名',
    `org_id`              VARCHAR(50)   NOT NULL DEFAULT '' COMMENT '用户所在的机构ID',
    `org_name`            VARCHAR(50)   NOT NULL DEFAULT '' COMMENT '用户所在的机构名称',
    `platform`            VARCHAR(50)   NOT NULL DEFAULT '' COMMENT '平台信息',
    `tenant_id`           VARCHAR(50)   NOT NULL DEFAULT '' COMMENT '租户ID',
    `trace_id`            VARCHAR(50)   NOT NULL DEFAULT '' COMMENT '跟踪ID',
    `request_api_uri`     VARCHAR(500)  NOT NULL DEFAULT '' COMMENT '请求的URI',
    `request_method`      VARCHAR(30)   NOT NULL DEFAULT '' COMMENT '请求的方法',
    `input`               TEXT                   DEFAULT NULL COMMENT '请求参数（JSON）',
    `output`              TEXT                   DEFAULT NULL COMMENT '返回结果（JSON）',
    `success`             TINYINT(4)    NOT NULL DEFAULT '1' COMMENT '是否执行成功：0 未成功，1 成功',
    `error`               TEXT                   DEFAULT NULL COMMENT '错误消息',
    `time_spent`          INT(8)                 DEFAULT NULL COMMENT '耗时（单位：毫秒）',
    `client_app_id`       VARCHAR(40)   NOT NULL DEFAULT '' COMMENT '客户端的应用的ID（标识是哪个应用）',
    `client_device_id`    VARCHAR(40)   NOT NULL DEFAULT '' COMMENT '客户端的设备的ID',
    `client_user_agent`   VARCHAR(500)  NOT NULL DEFAULT '' COMMENT '客户端的用户代理',
    `client_net_address`  VARCHAR(60)   NOT NULL DEFAULT '' COMMENT '客户端的网络地址',
    `client_geo_address`  VARCHAR(60)   NOT NULL DEFAULT '' COMMENT '客户端的地理地址',
    `client_geo_location` VARCHAR(200)  NOT NULL DEFAULT '' COMMENT '客户端的地理位置（经纬度JSON）',
    `server_name`         VARCHAR(40)   NOT NULL DEFAULT '' COMMENT '服务器的名称',
    `server_app_name`     VARCHAR(40)   NOT NULL DEFAULT '' COMMENT '服务器的应用名称',
    `data_json`           TEXT                   DEFAULT NULL COMMENT '其他数据的JSON',
    `delete_status`       TINYINT(4)    NOT NULL DEFAULT '0' COMMENT '删除状态：0 未删除，1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_name` (`name`) USING BTREE,
    KEY `idx_time` (`time`) USING BTREE,
    KEY `idx_user_id` (`user_id`) USING BTREE,
    KEY `idx_org_id` (`org_id`) USING BTREE,
    KEY `idx_platform` (`platform`) USING BTREE,
    KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY `idx_request_api_uri` (`request_api_uri`) USING BTREE,
    KEY `idx_success` (`success`) USING BTREE,
    KEY `idx_client_device_id` (`client_device_id`) USING BTREE,
    KEY `idx_client_net_address` (`client_net_address`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 ROW_FORMAT = DYNAMIC COMMENT ='操作日志表';

-- ------------------------------------------------------------
-- 数据变动日志表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `base_change_log` (
    `id`                BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `time`              DATETIME     NOT NULL COMMENT '时间',
    `user_id`           VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '用户ID',
    `user_type`         VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '用户类型',
    `user_display_name` VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '用户的展示名',
    `org_id`            VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '用户所在的机构ID',
    `org_name`          VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '用户所在的机构名称',
    `platform`          VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '平台信息',
    `tenant_id`         VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '租户ID',
    `business_id`       VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '业务ID',
    `business_type`     SMALLINT(4)  NOT NULL DEFAULT '0' COMMENT '业务类型：0 缺省',
    `message`           TEXT                  DEFAULT NULL COMMENT '消息',
    `server_name`       VARCHAR(40)  NOT NULL DEFAULT '' COMMENT '服务器的名称',
    `server_app_name`   VARCHAR(40)  NOT NULL DEFAULT '' COMMENT '服务器的应用名称',
    `data_json`         TEXT                  DEFAULT NULL COMMENT '其他数据的JSON',
    `delete_status`     TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除状态：0 未删除，1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_time` (`time`) USING BTREE,
    KEY `idx_user_id` (`user_id`) USING BTREE,
    KEY `idx_org_id` (`org_id`) USING BTREE,
    KEY `idx_platform` (`platform`) USING BTREE,
    KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
    KEY `idx_business_id` (`business_id`) USING BTREE,
    KEY `idx_business_type` (`business_type`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 ROW_FORMAT = DYNAMIC COMMENT ='数据变动日志表';

-- ------------------------------------------------------------
-- 运行日志表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `base_run_log` (
    `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `server_name`     VARCHAR(40)  NOT NULL DEFAULT '' COMMENT '服务器的名称',
    `server_app_name` VARCHAR(40)  NOT NULL DEFAULT '' COMMENT '服务器的应用名称',
    `time`            DATETIME     NOT NULL COMMENT '时间',
    `module`          VARCHAR(300) NOT NULL DEFAULT '' COMMENT '模块名',
    `message`         TEXT                  DEFAULT NULL COMMENT '消息',
    `success`         TINYINT(4)   NOT NULL DEFAULT '1' COMMENT '是否执行成功：0 未成功，1 成功',
    `error`           TEXT                  DEFAULT NULL COMMENT '错误消息',
    `data_json`       TEXT                  DEFAULT NULL COMMENT '其他数据的JSON',
    `delete_status`   TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除状态：0 未删除，1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_server_name` (`server_name`) USING BTREE,
    KEY `idx_server_app_name` (`server_app_name`) USING BTREE,
    KEY `idx_time` (`time`) USING BTREE,
    KEY `idx_success` (`success`) USING BTREE,
    KEY `idx_module` (`module`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 ROW_FORMAT = DYNAMIC COMMENT ='运行日志表';

-- ------------------------------------------------------------
-- 异步处理的任务表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `base_async_proc_task` (
    `id`                 BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `business_type`      INT(5)      NOT NULL DEFAULT '0' COMMENT '业务类型：0 缺省',
    `business_id`        BIGINT(20)           DEFAULT NULL COMMENT '业务ID',
    `task_type`          TINYINT(4)  NOT NULL DEFAULT '0' COMMENT '任务类型：0 缺省，1 导出，2 导入',
    `task_parameters`    TEXT                 DEFAULT NULL COMMENT '任务参数（JSON对象字符串）',
    `begin_time`         DATETIME             DEFAULT NULL COMMENT '开始时间',
    `end_time`           DATETIME             DEFAULT NULL COMMENT '结束时间',
    `original_files`     TEXT                 DEFAULT NULL COMMENT '原始文件（JSON对象数组）',
    `original_filename`  VARCHAR(200) NOT NULL DEFAULT '' COMMENT '原文件名称',
    `status`             TINYINT(4)  NOT NULL DEFAULT '0' COMMENT '状态：0 缺省，1 待导入|导出，2 前置处理，3 导入|导出中，4 超时，5 失败，6 成功',
    `executor_name`      VARCHAR(150) NOT NULL DEFAULT '' COMMENT '执行者名称',
    `exec_failure_count` INT(5)      NOT NULL DEFAULT '0' COMMENT '执行失败的次数',
    `success_count`      INT(11)              DEFAULT NULL COMMENT '成功条数',
    `failure_count`      INT(11)              DEFAULT NULL COMMENT '失败条数',
    `total_count`        INT(11)              DEFAULT NULL COMMENT '总条数',
    `result_file`        VARCHAR(500) NOT NULL DEFAULT '' COMMENT '结果文件信息',
    `error_message`      VARCHAR(500) NOT NULL DEFAULT '' COMMENT '错误信息（失败信息）',
    `platform`           VARCHAR(50) NOT NULL DEFAULT '' COMMENT '平台信息',
    `tenant_id`          VARCHAR(50) NOT NULL DEFAULT '' COMMENT '租户ID',
    `owner_id`           BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '数据的所属人ID',
    `own_org_id`         BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '数据的所属机构ID',
    `create_user`        BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建者',
    `create_time`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_user`        BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '修改者',
    `modify_time`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status`      TINYINT(4)  NOT NULL DEFAULT '0' COMMENT '删除状态：0 未删除，1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_business_id` (`business_id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 ROW_FORMAT = DYNAMIC COMMENT ='异步处理的任务表';
