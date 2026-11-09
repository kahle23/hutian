-- ============================================================
-- 壶天(hutian) 初始化脚本
-- 库名与 application-dev.yml 数据源保持一致
-- ============================================================

CREATE DATABASE IF NOT EXISTS `hutian_test` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `hutian_test`;

-- ------------------------------------------------------------
-- 用户表（骨架版，仅占位）
-- 注意：此处的 username/nickname 骨架版 sys_user 已被 sql/system.sql 取代。
-- system.sql 会 DROP TABLE IF EXISTS sys_user 后按真实口径（account/name/enabled 等）
-- 重建并写入种子数据（admin/admin123）。本段保留仅为建库完整性，执行顺序须为
-- init.sql（建库）→ system.sql（重建 sys_user + 其余 4 张表）→ basic.sql。
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`      VARCHAR(64)  NOT NULL COMMENT '用户名',
    `password`      VARCHAR(128) NOT NULL COMMENT '密码（加密存储）',
    `nickname`      VARCHAR(64)           DEFAULT NULL COMMENT '昵称',
    `avatar`        VARCHAR(255)          DEFAULT NULL COMMENT '头像地址',
    `platform`      VARCHAR(32)           DEFAULT NULL COMMENT '平台',
    `tenant_id`     VARCHAR(64)           DEFAULT NULL COMMENT '租户标识',
    `create_user`   VARCHAR(64)           DEFAULT NULL COMMENT '创建人',
    `create_time`   DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_user`   VARCHAR(64)           DEFAULT NULL COMMENT '修改人',
    `modify_time`   DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status` TINYINT      NOT NULL DEFAULT 0 COMMENT '删除状态(0未删/1已删)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';
