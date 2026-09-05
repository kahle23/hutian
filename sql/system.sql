-- ============================================================
-- 洞天(hutian) 系统域建表脚本
-- 参考 th-core-server system 模块的表结构重写（剥离机构/岗位/数据权限等历史包袱）
-- 在 init.sql（建库）之后执行；本脚本取代 init.sql 里的骨架版 sys_user
-- ============================================================

USE `hutian_test`;

-- ------------------------------------------------------------
-- 用户表
-- init.sql 的骨架版 sys_user（username/nickname 口径）已被真实实现取代，先删后建
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `account`       VARCHAR(64)  NOT NULL COMMENT '登录账号',
    `password`      VARCHAR(128) NOT NULL COMMENT '密码摘要（kunlun CryptoUtil.digestToHex 的 hex，非明文）',
    `name`          VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '姓名/昵称',
    `avatar`        VARCHAR(255) NOT NULL DEFAULT '' COMMENT '头像地址',
    `enabled`       TINYINT(4)   NOT NULL DEFAULT '1' COMMENT '是否启用：0 未启用，1 启用',
    `platform`      VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '平台',
    `tenant_id`     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '租户标识',
    `create_user`   VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_user`   VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '修改人',
    `modify_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status` TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除状态：0 未删除，1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_account` (`account`) USING BTREE,
    KEY `idx_name` (`name`) USING BTREE,
    KEY `idx_enabled` (`enabled`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 ROW_FORMAT = DYNAMIC COMMENT ='用户表';

-- ------------------------------------------------------------
-- 角色表
-- name 不加唯一索引：软删（delete_status）场景下同名角色会撞唯一键，防重在代码层做
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id`            BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`          VARCHAR(64) NOT NULL COMMENT '角色名称',
    `code`          VARCHAR(64) NOT NULL DEFAULT '' COMMENT '角色编码',
    `description`   VARCHAR(300) NOT NULL DEFAULT '' COMMENT '描述',
    `enabled`       TINYINT(4)  NOT NULL DEFAULT '1' COMMENT '是否启用：0 未启用，1 启用',
    `platform`      VARCHAR(32) NOT NULL DEFAULT '' COMMENT '平台',
    `tenant_id`     VARCHAR(64) NOT NULL DEFAULT '' COMMENT '租户标识',
    `create_user`   VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_user`   VARCHAR(64) NOT NULL DEFAULT '' COMMENT '修改人',
    `modify_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status` TINYINT(4)  NOT NULL DEFAULT '0' COMMENT '删除状态：0 未删除，1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_name` (`name`) USING BTREE,
    KEY `idx_code` (`code`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 ROW_FORMAT = DYNAMIC COMMENT ='角色表';

-- ------------------------------------------------------------
-- 菜单表（菜单兼权限点：type=3 按钮的 permission 即权限标识）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id`            BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id`     BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '父菜单ID（0=根）',
    `name`          VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `type`          TINYINT(4)  NOT NULL DEFAULT '0' COMMENT '菜单类型：0 未知，1 目录，2 菜单，3 按钮',
    `description`   VARCHAR(300) NOT NULL DEFAULT '' COMMENT '菜单描述',
    `icon`          VARCHAR(100) NOT NULL DEFAULT '' COMMENT '图标',
    `route`         VARCHAR(100) NOT NULL DEFAULT '' COMMENT '前端路由地址',
    `component`     VARCHAR(255) NOT NULL DEFAULT '' COMMENT '前端组件路径',
    `permission`    VARCHAR(100) NOT NULL DEFAULT '' COMMENT '权限标识（如 system:user:add）',
    `sort`          INT(6)      NOT NULL DEFAULT '0' COMMENT '排序（越小越靠前）',
    `enabled`       TINYINT(4)  NOT NULL DEFAULT '1' COMMENT '是否启用：0 未启用，1 启用',
    `show_flag`     TINYINT(4)  NOT NULL DEFAULT '1' COMMENT '是否显示：0 隐藏，1 显示',
    `platform`      VARCHAR(32) NOT NULL DEFAULT '' COMMENT '平台',
    `tenant_id`     VARCHAR(64) NOT NULL DEFAULT '' COMMENT '租户标识',
    `create_user`   VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_user`   VARCHAR(64) NOT NULL DEFAULT '' COMMENT '修改人',
    `modify_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status` TINYINT(4)  NOT NULL DEFAULT '0' COMMENT '删除状态：0 未删除，1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_parent_id` (`parent_id`) USING BTREE,
    KEY `idx_permission` (`permission`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 ROW_FORMAT = DYNAMIC COMMENT ='菜单表';

-- ------------------------------------------------------------
-- 用户角色表
-- (user_id, role_id) 不加唯一索引：软删场景下重绑会撞唯一键，防重在代码层做
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id`            BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`       BIGINT(20) NOT NULL COMMENT '用户ID',
    `role_id`       BIGINT(20) NOT NULL COMMENT '角色ID',
    `platform`      VARCHAR(32) NOT NULL DEFAULT '' COMMENT '平台',
    `tenant_id`     VARCHAR(64) NOT NULL DEFAULT '' COMMENT '租户标识',
    `create_user`   VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_user`   VARCHAR(64) NOT NULL DEFAULT '' COMMENT '修改人',
    `modify_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status` TINYINT(4)  NOT NULL DEFAULT '0' COMMENT '删除状态：0 未删除，1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_user_id` (`user_id`) USING BTREE,
    KEY `idx_role_id` (`role_id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 ROW_FORMAT = DYNAMIC COMMENT ='用户角色表';

-- ------------------------------------------------------------
-- 角色菜单表
-- (role_id, menu_id) 不加唯一索引：软删场景下重授会撞唯一键，防重在代码层做
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id`            BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id`       BIGINT(20) NOT NULL COMMENT '角色ID',
    `menu_id`       BIGINT(20) NOT NULL COMMENT '菜单ID',
    `platform`      VARCHAR(32) NOT NULL DEFAULT '' COMMENT '平台',
    `tenant_id`     VARCHAR(64) NOT NULL DEFAULT '' COMMENT '租户标识',
    `create_user`   VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_user`   VARCHAR(64) NOT NULL DEFAULT '' COMMENT '修改人',
    `modify_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status` TINYINT(4)  NOT NULL DEFAULT '0' COMMENT '删除状态：0 未删除，1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_role_id` (`role_id`) USING BTREE,
    KEY `idx_menu_id` (`menu_id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 ROW_FORMAT = DYNAMIC COMMENT ='角色菜单表';

-- ============================================================
-- 种子数据（幂等：按业务键 NOT EXISTS / 按固定主键 INSERT IGNORE，可重复执行）
-- 种子创建人统一标记为 system
-- ============================================================

-- ------------------------------------------------------------
-- 超级管理员角色
-- ------------------------------------------------------------
INSERT INTO `sys_role` (`name`, `code`, `description`, `enabled`, `create_user`)
SELECT '超级管理员', 'super_admin', '系统内置超级管理员角色，拥有全部权限', 1, 'system'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_role` WHERE `code` = 'super_admin' AND `delete_status` = 0);

-- ------------------------------------------------------------
-- admin 用户（账号 admin / 密码 admin123）
-- password 为 kunlun 1.0.27 CryptoUtil.digestToHex("admin123") 的真实结果：
-- 算法是默认 CustomDigester（DES加密+加盐+HMAC-SHA512+加盐+HMAC-SHA256 后取 hex），
-- 并非明文 SHA-256，请勿按 SHA-256 口径重算
-- ------------------------------------------------------------
INSERT INTO `sys_user` (`account`, `password`, `name`, `enabled`, `create_user`)
SELECT 'admin', 'c233fbabc2af58877f9cb81b7f87aeeb79664200a99288bf574ded4dc271872a', '管理员', 1, 'system'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_user` WHERE `account` = 'admin' AND `delete_status` = 0);

-- ------------------------------------------------------------
-- admin 绑定超级管理员角色
-- ------------------------------------------------------------
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `create_user`)
SELECT u.`id`, r.`id`, 'system'
FROM `sys_user` u, `sys_role` r
WHERE u.`account` = 'admin' AND u.`delete_status` = 0
  AND r.`code` = 'super_admin' AND r.`delete_status` = 0
  AND NOT EXISTS (SELECT 1 FROM `sys_user_role` ur
                  WHERE ur.`user_id` = u.`id` AND ur.`role_id` = r.`id` AND ur.`delete_status` = 0);

-- ------------------------------------------------------------
-- 种子菜单：固定主键便于父子引用与角色绑定，重复执行由 INSERT IGNORE 幂等
-- 1 系统管理（目录）
--   10 用户管理（菜单）+ 11~13 新增/编辑/删除（按钮）
--   20 角色管理（菜单）+ 21~23 新增/编辑/删除（按钮）
--   30 菜单管理（菜单）+ 31~33 新增/编辑/删除（按钮）
-- ------------------------------------------------------------
INSERT IGNORE INTO `sys_menu`
(`id`, `parent_id`, `name`, `type`, `description`, `icon`, `route`, `component`, `permission`, `sort`, `enabled`, `show_flag`, `create_user`)
VALUES
(1,  0,  '系统管理', 1, '系统管理目录', 'setting', 'system', '', '', 1, 1, 1, 'system'),
(10, 1,  '用户管理', 2, '用户的维护', 'user', 'user', 'system/user/index', 'system:user', 1, 1, 1, 'system'),
(11, 10, '用户新增', 3, '', '', '', '', 'system:user:add', 1, 1, 0, 'system'),
(12, 10, '用户编辑', 3, '', '', '', '', 'system:user:edit', 2, 1, 0, 'system'),
(13, 10, '用户删除', 3, '', '', '', '', 'system:user:delete', 3, 1, 0, 'system'),
(20, 1,  '角色管理', 2, '角色的维护', 'peoples', 'role', 'system/role/index', 'system:role', 2, 1, 1, 'system'),
(21, 20, '角色新增', 3, '', '', '', '', 'system:role:add', 1, 1, 0, 'system'),
(22, 20, '角色编辑', 3, '', '', '', '', 'system:role:edit', 2, 1, 0, 'system'),
(23, 20, '角色删除', 3, '', '', '', '', 'system:role:delete', 3, 1, 0, 'system'),
(30, 1,  '菜单管理', 2, '菜单的维护', 'tree', 'menu', 'system/menu/index', 'system:menu', 3, 1, 1, 'system'),
(31, 30, '菜单新增', 3, '', '', '', '', 'system:menu:add', 1, 1, 0, 'system'),
(32, 30, '菜单编辑', 3, '', '', '', '', 'system:menu:edit', 2, 1, 0, 'system'),
(33, 30, '菜单删除', 3, '', '', '', '', 'system:menu:delete', 3, 1, 0, 'system');

-- ------------------------------------------------------------
-- 超级管理员角色绑定全部菜单（含后续手工新增的菜单可重跑本段补授）
-- ------------------------------------------------------------
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_user`)
SELECT r.`id`, m.`id`, 'system'
FROM `sys_role` r, `sys_menu` m
WHERE r.`code` = 'super_admin' AND r.`delete_status` = 0
  AND m.`delete_status` = 0
  AND NOT EXISTS (SELECT 1 FROM `sys_role_menu` rm
                  WHERE rm.`role_id` = r.`id` AND rm.`menu_id` = m.`id` AND rm.`delete_status` = 0);
