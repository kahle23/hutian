-- ============================================================
-- 壶天(hutian) 内容服务域建表脚本（hutian-cms）
-- 对齐 docs/cms-design.md §1 契约，建表规范对齐 docs/sql/basic.sql
-- 在 init.sql（建库）之后执行；全部 CREATE TABLE IF NOT EXISTS + 种子幂等，可重复执行
-- ============================================================

USE `hutian_test`;

-- ------------------------------------------------------------
-- 内容分类表（平铺，无 parent）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cms_category` (
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`          VARCHAR(100) NOT NULL COMMENT '分类名称',
    `code`          VARCHAR(64)           DEFAULT NULL COMMENT '分类编码（唯一性代码层校验，软删行不占位）',
    `type`          TINYINT(4)   NOT NULL DEFAULT '1' COMMENT '内容类型 0缺省/1文章/11版本/12公告/13帮助/14提示',
    `icon`          VARCHAR(64)           DEFAULT NULL COMMENT '图标',
    `sort`          INT(8)       NOT NULL DEFAULT '0' COMMENT '排序（升序，越小越前）',
    `enabled`       TINYINT(4)   NOT NULL DEFAULT '1' COMMENT '是否启用 0禁用/1启用',
    `description`   VARCHAR(255)          DEFAULT NULL COMMENT '描述',
    `platform`      VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '平台信息',
    `tenant_id`     VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '租户ID',
    `create_user`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '创建者',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_user`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '修改者',
    `modify_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status` TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除状态：0 未删除，1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_type_enabled` (`type`, `enabled`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 ROW_FORMAT = DYNAMIC COMMENT ='CMS-内容分类';

-- ------------------------------------------------------------
-- 内容表（文章/公告/版本/帮助/提示等所有内容共用）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cms_article` (
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `type`          TINYINT(4)   NOT NULL DEFAULT '1' COMMENT '内容类型（同 cms_category.type）',
    `category_id`   BIGINT(20)            DEFAULT NULL COMMENT '分类id（NULL=未分类）',
    `title`         VARCHAR(200) NOT NULL COMMENT '标题',
    `cover_images`  TEXT                  DEFAULT NULL COMMENT '封面图 JSON数组 [{name,addr}]',
    `summary`       VARCHAR(500) NOT NULL DEFAULT '' COMMENT '摘要（空则按正文提纯截前200字）',
    `top_weight`    INT(8)       NOT NULL DEFAULT '0' COMMENT '置顶权重 0不置顶/>0越大越前',
    `content_type`  TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '正文格式 0缺省/1纯文本/2Markdown/3Html',
    `content`       LONGTEXT              DEFAULT NULL COMMENT '正文',
    `attachments`   TEXT                  DEFAULT NULL COMMENT '附件 JSON数组 [{name,addr}]',
    `status`        TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '状态 0草稿/1已发布/2已下线',
    `publish_time`  DATETIME              DEFAULT NULL COMMENT '发布时间（未来时间=定时发布）',
    `publish_user`  BIGINT(20)            DEFAULT NULL COMMENT '发布人',
    `expire_time`   DATETIME              DEFAULT NULL COMMENT '下线时间（NULL=长期有效）',
    `view_count`    INT(11)      NOT NULL DEFAULT '0' COMMENT '浏览数',
    `platform`      VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '平台信息',
    `tenant_id`     VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '租户ID',
    `create_user`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '创建者',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_user`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '修改者',
    `modify_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status` TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除状态：0 未删除，1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_status_pub` (`status`, `publish_time`) USING BTREE,
    KEY `idx_category` (`category_id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 ROW_FORMAT = DYNAMIC COMMENT ='CMS-内容';

-- ------------------------------------------------------------
-- 内容生命周期留痕表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cms_article_log` (
    `id`            BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id`    BIGINT(20)  NOT NULL COMMENT '文章id',
    `action_type`   TINYINT(4)  NOT NULL COMMENT '动作 1创建/2发布/3下线/4置顶/5取消置顶/6编辑/7复制',
    `operator_id`   BIGINT(20)           DEFAULT NULL COMMENT '操作人（NULL=定时任务）',
    `action_time`   DATETIME    NOT NULL COMMENT '动作时间',
    `detail`        VARCHAR(500)         DEFAULT NULL COMMENT '详情文案',
    `platform`      VARCHAR(50) NOT NULL DEFAULT '' COMMENT '平台信息',
    `tenant_id`     VARCHAR(50) NOT NULL DEFAULT '' COMMENT '租户ID',
    `create_user`   BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建者',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_user`   BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '修改者',
    `modify_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status` TINYINT(4)  NOT NULL DEFAULT '0' COMMENT '删除状态：0 未删除，1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_article` (`article_id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 ROW_FORMAT = DYNAMIC COMMENT ='CMS-内容留痕';

-- ------------------------------------------------------------
-- 内容已读表（触达统计与门户已读未读的数据源；一人一文一条）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cms_article_read` (
    `id`            BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id`    BIGINT(20)  NOT NULL COMMENT '文章id',
    `user_id`       BIGINT(20)  NOT NULL COMMENT '用户id',
    `read_time`     DATETIME    NOT NULL COMMENT '首次阅读时间',
    `platform`      VARCHAR(50) NOT NULL DEFAULT '' COMMENT '平台信息',
    `tenant_id`     VARCHAR(50) NOT NULL DEFAULT '' COMMENT '租户ID',
    `create_user`   BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建者',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_user`   BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '修改者',
    `modify_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `delete_status` TINYINT(4)  NOT NULL DEFAULT '0' COMMENT '删除状态：0 未删除，1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_article_user` (`article_id`, `user_id`) USING BTREE,
    KEY `idx_user` (`user_id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 ROW_FORMAT = DYNAMIC COMMENT ='CMS-内容已读';

-- ------------------------------------------------------------
-- 幂等种子：公告分类两条（固定 id 1/2）
-- ------------------------------------------------------------
INSERT INTO `cms_category`
    (`id`, `name`, `code`, `type`, `icon`, `sort`, `enabled`, `description`,
     `platform`, `tenant_id`, `create_user`, `create_time`, `modify_user`, `modify_time`, `delete_status`)
SELECT 1, '公司公告', 'company-notice', 12, NULL, 0, 1, '面向全员的公司公告',
       'hutian', '0', 0, NOW(), 0, NOW(), 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `cms_category` WHERE `id` = 1);

INSERT INTO `cms_category`
    (`id`, `name`, `code`, `type`, `icon`, `sort`, `enabled`, `description`,
     `platform`, `tenant_id`, `create_user`, `create_time`, `modify_user`, `modify_time`, `delete_status`)
SELECT 2, '产品动态', 'product-news', 1, NULL, 0, 1, '产品版本与动态',
       'hutian', '0', 0, NOW(), 0, NOW(), 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `cms_category` WHERE `id` = 2);

-- ------------------------------------------------------------
-- 幂等种子：示例内容（固定 id 1001=已发布公告（发布时间=昨天）/ 1002=草稿文章）
-- ------------------------------------------------------------
INSERT INTO `cms_article`
    (`id`, `type`, `category_id`, `title`, `cover_images`, `summary`, `top_weight`, `content_type`,
     `content`, `attachments`, `status`, `publish_time`, `publish_user`, `expire_time`, `view_count`,
     `platform`, `tenant_id`, `create_user`, `create_time`, `modify_user`, `modify_time`, `delete_status`)
SELECT 1001, 12, 1, '欢迎使用壶天内容中心', NULL,
       '壶天内容中心已上线：支持文章与公告的草稿、发布、置顶、留痕、已读未读与触达统计，欢迎体验。',
       0, 1,
       '壶天内容中心已上线。支持内容类型：文章、公告、版本、帮助、提示；支持草稿、定时发布、置顶、复制与生命周期留痕；门户侧支持已读未读、触达统计与全文搜索。', NULL,
       1, DATE_SUB(NOW(), INTERVAL 1 DAY), 1, NULL, 0,
       'hutian', '0', 1, NOW(), 1, NOW(), 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `cms_article` WHERE `id` = 1001);

INSERT INTO `cms_article`
    (`id`, `type`, `category_id`, `title`, `cover_images`, `summary`, `top_weight`, `content_type`,
     `content`, `attachments`, `status`, `publish_time`, `publish_user`, `expire_time`, `view_count`,
     `platform`, `tenant_id`, `create_user`, `create_time`, `modify_user`, `modify_time`, `delete_status`)
SELECT 1002, 1, 2, '产品动态示例（草稿）', NULL, '', 0, 1,
       '这是一篇产品动态草稿示例：保存后可在内容管理中编辑、发布。', NULL,
       0, NULL, NULL, NULL, 0,
       'hutian', '0', 1, NOW(), 1, NOW(), 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `cms_article` WHERE `id` = 1002);
