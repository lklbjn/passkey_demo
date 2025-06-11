/*
 Navicat Premium Data Transfer

 Source Server         : DB1
 Source Server Type    : MySQL
 Source Server Version : 80029
 Source Host           : localhost:3306
 Source Schema         : passkey_demo

 Target Server Type    : MySQL
 Target Server Version : 80029
 File Encoding         : 65001

 Date: 11/06/2025 15:41:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `user_handle` varbinary(64) NOT NULL COMMENT '用户句柄',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_handle`(`user_handle`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'admin@wonde.cc', 0x8E77B45C2AB87FC364B7D0BD737A16128F6845AEC572D98338B197C858353896, '2025-06-06 13:52:20', '2025-06-10 15:35:19');

-- ----------------------------
-- Table structure for webauthn_credential
-- ----------------------------
DROP TABLE IF EXISTS `webauthn_credential`;
CREATE TABLE `webauthn_credential`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `display_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '展示名称',
  `credential_id` varbinary(255) NOT NULL COMMENT '凭证信息',
  `public_key_cose` varbinary(1024) NOT NULL COMMENT '公钥',
  `signature_count` bigint(0) NOT NULL COMMENT '防重放攻击计数',
  `transports_json` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '传输方式',
  `registration_time` datetime(0) NOT NULL COMMENT '注册时间',
  `backup_eligible` tinyint(1) NOT NULL COMMENT '凭证是否可以备份',
  `backup_state` tinyint(1) NOT NULL COMMENT '是否已备份',
  `credential_registration_request` json NULL COMMENT '认证request',
  `credential_registration_result` json NULL COMMENT '认证result',
  `describe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '认证表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of webauthn_credential
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
