-- =====================================================
-- 钓鱼渔获记录与位点收藏系统 数据库脚本
-- Database: MySQL 8.0+
-- =====================================================

CREATE DATABASE IF NOT EXISTS fishing_catch_spot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fishing_catch_spot;

-- =====================================================
-- 1. 用户表（钓友信息）
-- =====================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    user_id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username         VARCHAR(50)  NOT NULL COMMENT '用户名(登录账号)',
    nickname         VARCHAR(50)  NOT NULL COMMENT '昵称',
    password         VARCHAR(100) NOT NULL COMMENT '密码',
    avatar           VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    phone            VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    email            VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    status           TINYINT      NOT NULL DEFAULT 1 COMMENT '状态:1-启用,0-禁用',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted          TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 2. 鱼种字典表
-- =====================================================
DROP TABLE IF EXISTS fish_species;
CREATE TABLE fish_species (
    species_id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '鱼种ID',
    species_name     VARCHAR(50)  NOT NULL COMMENT '鱼种名称',
    species_code     VARCHAR(30)  NOT NULL COMMENT '鱼种编码',
    description      VARCHAR(500) DEFAULT NULL COMMENT '描述',
    sort_order       INT          NOT NULL DEFAULT 0 COMMENT '排序',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (species_id),
    UNIQUE KEY uk_species_code (species_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鱼种字典表';

-- =====================================================
-- 3. 钓点/位点表（核心表）
-- =====================================================
DROP TABLE IF EXISTS fishing_spot;
CREATE TABLE fishing_spot (
    spot_id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '钓点ID',
    user_id          BIGINT       NOT NULL COMMENT '创建用户ID',
    spot_name        VARCHAR(100) NOT NULL COMMENT '钓点名称',
    water_type       TINYINT      NOT NULL COMMENT '水域类型:1-水库,2-湖,3-河,4-海',
    longitude        DECIMAL(10,6) NOT NULL COMMENT '经度(精确坐标)',
    latitude         DECIMAL(10,6) NOT NULL COMMENT '纬度(精确坐标)',
    address          VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
    description      VARCHAR(1000) DEFAULT NULL COMMENT '钓点描述',
    images           JSON         DEFAULT NULL COMMENT '钓点图片(JSON数组)',
    is_shared        TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已分享:0-否,1-是',
    is_favorite      TINYINT      NOT NULL DEFAULT 1 COMMENT '是否收藏:0-否,1-是',
    record_count     INT          NOT NULL DEFAULT 0 COMMENT '出钓记录数',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted          TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
    PRIMARY KEY (spot_id),
    KEY idx_user_id (user_id),
    KEY idx_water_type (water_type),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钓点/位点表';

-- =====================================================
-- 4. 出钓记录表
-- =====================================================
DROP TABLE IF EXISTS fishing_record;
CREATE TABLE fishing_record (
    record_id        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    spot_id          BIGINT       NOT NULL COMMENT '钓点ID',
    user_id          BIGINT       NOT NULL COMMENT '用户ID',
    fishing_date     DATE         NOT NULL COMMENT '出钓日期',
    start_time       TIME         DEFAULT NULL COMMENT '开始时间',
    end_time         TIME         DEFAULT NULL COMMENT '结束时间',
    fishing_method   TINYINT      NOT NULL COMMENT '钓法:1-手竿,2-矶竿,3-路亚,4-海钓',
    bait             VARCHAR(200) DEFAULT NULL COMMENT '使用的饵料',
    weather          VARCHAR(100) DEFAULT NULL COMMENT '天气情况',
    temperature      VARCHAR(30)  DEFAULT NULL COMMENT '气温',
    wind_direction   VARCHAR(30)  DEFAULT NULL COMMENT '风向',
    wind_level       VARCHAR(30)  DEFAULT NULL COMMENT '风力等级',
    water_level      VARCHAR(100) DEFAULT NULL COMMENT '水位情况',
    total_count      INT          NOT NULL DEFAULT 0 COMMENT '总渔获数量',
    total_weight     DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '总渔获重量(kg)',
    remark           VARCHAR(1000) DEFAULT NULL COMMENT '备注/心得',
    images           JSON         DEFAULT NULL COMMENT '渔获图片(JSON数组)',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted          TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
    PRIMARY KEY (record_id),
    KEY idx_spot_id (spot_id),
    KEY idx_user_id (user_id),
    KEY idx_fishing_date (fishing_date),
    KEY idx_user_date (user_id, fishing_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出钓记录表';

-- =====================================================
-- 5. 渔获明细表
-- =====================================================
DROP TABLE IF EXISTS catch_detail;
CREATE TABLE catch_detail (
    detail_id        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    record_id        BIGINT       NOT NULL COMMENT '出钓记录ID',
    species_id       BIGINT       NOT NULL COMMENT '鱼种ID',
    species_name     VARCHAR(50)  NOT NULL COMMENT '鱼种名称(冗余)',
    catch_count      INT          NOT NULL DEFAULT 0 COMMENT '该鱼种数量',
    total_weight     DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '该鱼种总重量(kg)',
    max_weight       DECIMAL(10,2) DEFAULT NULL COMMENT '单条最大重量(kg)',
    remark           VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (detail_id),
    KEY idx_record_id (record_id),
    KEY idx_species_id (species_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='渔获明细表';

-- =====================================================
-- 6. 位点分享表
-- =====================================================
DROP TABLE IF EXISTS spot_share;
CREATE TABLE spot_share (
    share_id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分享ID',
    spot_id          BIGINT       NOT NULL COMMENT '钓点ID',
    share_user_id    BIGINT       NOT NULL COMMENT '分享用户ID',
    share_title      VARCHAR(200) DEFAULT NULL COMMENT '分享标题',
    share_content    VARCHAR(2000) DEFAULT NULL COMMENT '分享内容',
    blurred_lng      DECIMAL(10,6) NOT NULL COMMENT '模糊化后经度(公里级)',
    blurred_lat      DECIMAL(10,6) NOT NULL COMMENT '模糊化后纬度(公里级)',
    view_count       INT          NOT NULL DEFAULT 0 COMMENT '浏览次数',
    like_count       INT          NOT NULL DEFAULT 0 COMMENT '点赞数',
    share_status     TINYINT      NOT NULL DEFAULT 1 COMMENT '分享状态:1-正常,0-下架',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分享时间',
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted          TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
    PRIMARY KEY (share_id),
    KEY idx_spot_id (spot_id),
    KEY idx_share_user_id (share_user_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='位点分享表';

-- =====================================================
-- 7. 钓点收藏表（收藏他人分享的钓点）
-- =====================================================
DROP TABLE IF EXISTS spot_favorite;
CREATE TABLE spot_favorite (
    favorite_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    user_id          BIGINT       NOT NULL COMMENT '用户ID',
    share_id         BIGINT       NOT NULL COMMENT '分享记录ID',
    spot_id          BIGINT       NOT NULL COMMENT '钓点ID',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (favorite_id),
    UNIQUE KEY uk_user_share (user_id, share_id),
    KEY idx_user_id (user_id),
    KEY idx_spot_id (spot_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钓点收藏表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 初始化鱼种数据
INSERT INTO fish_species (species_name, species_code, description, sort_order) VALUES
('鲫鱼', 'crucian', '常见淡水鱼，体型较小，肉质细嫩', 1),
('鲤鱼', 'carp', '淡水鱼，体型较大，适应性强', 2),
('草鱼', 'grass_carp', '草食性淡水鱼，生长快', 3),
('鲢鱼', 'silver_carp', '滤食性淡水鱼，喜肥水', 4),
('鳙鱼', 'bighead', '又名花鲢，大头鱼', 5),
('青鱼', 'black_carp', '肉食性淡水鱼，体型大', 6),
('黑鱼', 'snakehead', '凶猛肉食性鱼类', 7),
('鲶鱼', 'catfish', '无鳞淡水鱼，夜行性', 8),
('黄颡鱼', 'yellow_catfish', '又名黄骨鱼，肉质鲜美', 9),
('罗非鱼', 'tilapia', '热带淡水鱼，适应性强', 10),
('鲈鱼', 'bass', '淡水鲈鱼，肉质细嫩', 11),
('鳜鱼', 'mandarin_fish', '名贵淡水鱼，肉质极佳', 12),
('翘嘴鲌', 'topmouth_culter', '又名大白鱼，肉食性', 13),
('马口鱼', 'opsariichthys', '小型溪流鱼', 14),
('白条鱼', 'white_snack', '常见小型上层鱼', 15),
('带鱼', 'hairtail', '海鱼，体型侧扁如带', 16),
('黄鱼', 'yellow_croaker', '海鱼，分大小黄鱼', 17),
('石斑鱼', 'grouper', '海水名贵鱼种', 18),
('真鲷', 'red_sea_bream', '海水鱼，肉质鲜美', 19),
('黑鲷', 'black_sea_bream', '海水鱼，适应性强', 20);

-- 初始化测试用户 (密码: 123456 BCrypt加密)
INSERT INTO sys_user (username, nickname, password, phone, email) VALUES
('fisher001', '老钓手', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '13800138001', 'fisher001@example.com'),
('fisher002', '路亚小哥', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '13800138002', 'fisher002@example.com');
