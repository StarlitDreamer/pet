-- ============================================================
-- 宠物寄养管理系统 - 数据库初始化脚本
-- Pet Boarding Management System - Database Initialization
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `pet_boarding` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `pet_boarding`;

-- -----------------------------------------------------------
-- 1. 用户表 (user)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码(MD5)',
  `role` TINYINT NOT NULL DEFAULT 1 COMMENT '角色: 1-宠物主人 2-服务商',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
  `verified` TINYINT NOT NULL DEFAULT 0 COMMENT '实名认证: 0-未认证 1-已认证',
  `points` INT NOT NULL DEFAULT 0 COMMENT '积分',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- -----------------------------------------------------------
-- 2. 宠物表 (pet)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `pet`;
CREATE TABLE `pet` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
  `owner_id` BIGINT NOT NULL COMMENT '主人ID',
  `name` VARCHAR(50) NOT NULL COMMENT '宠物名',
  `species` VARCHAR(20) NOT NULL COMMENT '物种(猫/狗等)',
  `breed` VARCHAR(50) DEFAULT NULL COMMENT '品种',
  `gender` TINYINT DEFAULT NULL COMMENT '性别: 0-未知 1-公 2-母',
  `age` INT DEFAULT NULL COMMENT '年龄(月)',
  `weight` DECIMAL(5,2) DEFAULT NULL COMMENT '体重(kg)',
  `photo` VARCHAR(255) DEFAULT NULL COMMENT '照片URL',
  `health_status` VARCHAR(255) DEFAULT NULL COMMENT '健康状况',
  `vaccination` VARCHAR(255) DEFAULT NULL COMMENT '疫苗接种情况',
  `allergies` VARCHAR(255) DEFAULT NULL COMMENT '过敏信息',
  `habits` TEXT DEFAULT NULL COMMENT '生活习惯',
  `emergency_contact` VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-已删除 1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='宠物表';

-- -----------------------------------------------------------
-- 3. 店铺表 (shop)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '店铺ID',
  `provider_id` BIGINT NOT NULL COMMENT '服务商用户ID',
  `name` VARCHAR(100) NOT NULL COMMENT '店铺名称',
  `description` TEXT DEFAULT NULL COMMENT '店铺描述',
  `logo` VARCHAR(255) DEFAULT NULL COMMENT 'Logo URL',
  `photos` VARCHAR(1000) DEFAULT NULL COMMENT '店铺照片(JSON数组)',
  `province` VARCHAR(50) DEFAULT NULL COMMENT '省份',
  `city` VARCHAR(50) DEFAULT NULL COMMENT '城市',
  `district` VARCHAR(50) DEFAULT NULL COMMENT '区县',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
  `longitude` DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `business_license` VARCHAR(255) DEFAULT NULL COMMENT '营业执照URL',
  `qualification` VARCHAR(255) DEFAULT NULL COMMENT '资质证书URL',
  `capacity` INT NOT NULL DEFAULT 0 COMMENT '最大容纳数',
  `current_count` INT NOT NULL DEFAULT 0 COMMENT '当前寄养数',
  `rating` DECIMAL(2,1) NOT NULL DEFAULT 5.0 COMMENT '评分',
  `status` TINYINT NOT NULL DEFAULT 2 COMMENT '状态: 0-关闭 1-营业 2-审核中',
  `verified` TINYINT NOT NULL DEFAULT 0 COMMENT '资质认证: 0-未认证 1-已认证',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_provider_id` (`provider_id`),
  KEY `idx_city` (`city`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='店铺表';

-- -----------------------------------------------------------
-- 4. 服务项目表 (service_item)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `service_item`;
CREATE TABLE `service_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '服务项目ID',
  `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
  `name` VARCHAR(100) NOT NULL COMMENT '服务名称',
  `description` TEXT DEFAULT NULL COMMENT '服务描述',
  `species` VARCHAR(20) DEFAULT NULL COMMENT '适用物种',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `unit` VARCHAR(20) NOT NULL DEFAULT '天' COMMENT '计价单位',
  `includes` VARCHAR(500) DEFAULT NULL COMMENT '服务包含内容',
  `photo` VARCHAR(255) DEFAULT NULL COMMENT '服务图片URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_species` (`species`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='服务项目表';

-- -----------------------------------------------------------
-- 5. 订单表 (order_info)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
  `owner_id` BIGINT NOT NULL COMMENT '宠物主人ID',
  `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
  `pet_id` BIGINT NOT NULL COMMENT '宠物ID',
  `service_id` BIGINT NOT NULL COMMENT '服务项目ID',
  `start_date` DATE NOT NULL COMMENT '寄养开始日期',
  `end_date` DATE NOT NULL COMMENT '寄养结束日期',
  `days` INT NOT NULL COMMENT '寄养天数',
  `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '总金额',
  `discount_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
  `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
  `insurance` TINYINT NOT NULL DEFAULT 0 COMMENT '是否购买保险: 0-否 1-是',
  `insurance_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '保险金额',
  `coupon_id` BIGINT DEFAULT NULL COMMENT '优惠券ID',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待支付 1-待接单 2-已接单 3-已入住 4-寄养中 5-已退房 6-已完成 7-已取消 8-已拒绝',
  `cancel_reason` VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
  `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '拒绝原因',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `accept_time` DATETIME DEFAULT NULL COMMENT '接单时间',
  `checkin_time` DATETIME DEFAULT NULL COMMENT '入住时间',
  `checkout_time` DATETIME DEFAULT NULL COMMENT '退房时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单表';

-- -----------------------------------------------------------
-- 6. 照料记录表 (care_record)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `care_record`;
CREATE TABLE `care_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `pet_id` BIGINT NOT NULL COMMENT '宠物ID',
  `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
  `employee_id` BIGINT DEFAULT NULL COMMENT '员工ID',
  `type` TINYINT NOT NULL COMMENT '类型: 1-日常 2-喂食 3-运动 4-清洁 5-健康',
  `content` TEXT NOT NULL COMMENT '照料内容',
  `photos` VARCHAR(1000) DEFAULT NULL COMMENT '照片(JSON数组)',
  `video` VARCHAR(255) DEFAULT NULL COMMENT '视频URL',
  `health_status` VARCHAR(100) DEFAULT NULL COMMENT '健康状态',
  `health_note` VARCHAR(500) DEFAULT NULL COMMENT '健康备注',
  `temperature` DECIMAL(4,1) DEFAULT NULL COMMENT '体温',
  `weight` DECIMAL(5,2) DEFAULT NULL COMMENT '体重(kg)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_type` (`type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='照料记录表';

-- -----------------------------------------------------------
-- 7. 评价表 (review)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `owner_id` BIGINT NOT NULL COMMENT '评价人ID',
  `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
  `rating` TINYINT NOT NULL COMMENT '评分(1-5)',
  `content` TEXT DEFAULT NULL COMMENT '评价内容',
  `photos` VARCHAR(1000) DEFAULT NULL COMMENT '评价照片(JSON数组)',
  `reply` TEXT DEFAULT NULL COMMENT '商家回复',
  `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-隐藏 1-显示',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='评价表';

-- -----------------------------------------------------------
-- 8. 投诉表 (complaint)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `complaint`;
CREATE TABLE `complaint` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '投诉ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `complainant_id` BIGINT NOT NULL COMMENT '投诉人ID',
  `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
  `type` TINYINT NOT NULL COMMENT '类型: 1-服务质量 2-宠物健康 3-费用争议 4-其他',
  `content` TEXT NOT NULL COMMENT '投诉内容',
  `photos` VARCHAR(1000) DEFAULT NULL COMMENT '投诉照片(JSON数组)',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待处理 1-处理中 2-已解决 3-已驳回',
  `reply` TEXT DEFAULT NULL COMMENT '处理回复',
  `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
  `result` VARCHAR(500) DEFAULT NULL COMMENT '处理结果',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_complainant_id` (`complainant_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='投诉表';

-- -----------------------------------------------------------
-- 9. 员工表 (employee)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
  `name` VARCHAR(50) NOT NULL COMMENT '员工姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `position` VARCHAR(50) DEFAULT NULL COMMENT '职位',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `entry_date` DATE DEFAULT NULL COMMENT '入职日期',
  `salary` DECIMAL(10,2) DEFAULT NULL COMMENT '薪资',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-离职 1-在职',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='员工表';

-- -----------------------------------------------------------
-- 10. 考勤表 (attendance)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '考勤ID',
  `employee_id` BIGINT NOT NULL COMMENT '员工ID',
  `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
  `date` DATE NOT NULL COMMENT '考勤日期',
  `clock_in` DATETIME DEFAULT NULL COMMENT '上班打卡时间',
  `clock_out` DATETIME DEFAULT NULL COMMENT '下班打卡时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-未打卡 1-正常 2-迟到 3-早退 4-缺勤 5-请假',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_date` (`date`),
  UNIQUE KEY `uk_employee_date` (`employee_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='考勤表';

-- -----------------------------------------------------------
-- 11. 优惠券模板表 (coupon)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
  `name` VARCHAR(100) NOT NULL COMMENT '优惠券名称',
  `type` TINYINT NOT NULL COMMENT '类型: 1-固定金额 2-折扣 3-免费天数',
  `value` DECIMAL(10,2) NOT NULL COMMENT '优惠值(金额/折扣率/天数)',
  `min_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '最低消费金额',
  `total_count` INT NOT NULL DEFAULT 0 COMMENT '发放总量',
  `remain_count` INT NOT NULL DEFAULT 0 COMMENT '剩余数量',
  `start_time` DATETIME NOT NULL COMMENT '生效时间',
  `end_time` DATETIME NOT NULL COMMENT '失效时间',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-停用 1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='优惠券模板表';

-- -----------------------------------------------------------
-- 12. 用户优惠券表 (user_coupon)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `coupon_id` BIGINT NOT NULL COMMENT '优惠券模板ID',
  `order_id` BIGINT DEFAULT NULL COMMENT '使用的订单ID',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-未使用 1-已使用 2-已过期',
  `receive_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `use_time` DATETIME DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_coupon_id` (`coupon_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户优惠券表';

-- -----------------------------------------------------------
-- 13. 积分记录表 (points_record)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `points_record`;
CREATE TABLE `points_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` TINYINT NOT NULL COMMENT '类型: 1-获取 2-消费 3-过期',
  `points` INT NOT NULL COMMENT '积分数',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
  `order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分记录表';

-- -----------------------------------------------------------
-- 14. 消息通知表 (message)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` BIGINT DEFAULT NULL COMMENT '发送者ID(系统消息为NULL)',
  `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
  `order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
  `type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型: 1-系统 2-订单 3-照料 4-健康 5-聊天',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读: 0-未读 1-已读',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_type` (`type`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='消息通知表';

-- ============================================================
-- 测试数据 (Test Data)
-- ============================================================

-- -----------------------------------------------------------
-- 用户数据: 3个宠物主人 + 2个服务商
-- -----------------------------------------------------------
INSERT INTO `user` (`id`, `username`, `password`, `role`, `real_name`, `id_card`, `phone`, `email`, `avatar`, `status`, `verified`, `points`, `create_time`) VALUES
(1, 'owner_zhang',  'e10adc3949ba59abbe56e057f20f883e', 1, '张三', '110101199001011234', '13800001001', 'zhangsan@example.com',  '/avatars/user1.jpg', 1, 1, 260, '2025-01-10 08:00:00'),
(2, 'owner_li',     'e10adc3949ba59abbe56e057f20f883e', 1, '李四', '110101199202022345', '13800001002', 'lisi@example.com',      '/avatars/user2.jpg', 1, 1, 150, '2025-02-15 09:30:00'),
(3, 'owner_wang',   'e10adc3949ba59abbe56e057f20f883e', 1, '王五', '110101199503033456', '13800001003', 'wangwu@example.com',    '/avatars/user3.jpg', 1, 0,  50, '2025-03-20 10:00:00'),
(4, 'provider_sun', 'e10adc3949ba59abbe56e057f20f883e', 2, '孙六', '310101198804044567', '13900002001', 'sunliu@example.com',    '/avatars/user4.jpg', 1, 1,   0, '2025-01-05 07:00:00'),
(5, 'provider_zhao','e10adc3949ba59abbe56e057f20f883e', 2, '赵七', '440101198705055678', '13900002002', 'zhaoqi@example.com',    '/avatars/user5.jpg', 1, 1,   0, '2025-01-08 08:30:00');

-- -----------------------------------------------------------
-- 宠物数据: 4只宠物
-- -----------------------------------------------------------
INSERT INTO `pet` (`id`, `owner_id`, `name`, `species`, `breed`, `gender`, `age`, `weight`, `photo`, `health_status`, `vaccination`, `allergies`, `habits`, `emergency_contact`, `emergency_phone`, `status`, `create_time`) VALUES
(1, 1, '豆豆', '狗', '金毛寻回犬', 1, 36, 28.50, '/pets/pet1.jpg', '健康', '狂犬疫苗、六联疫苗均已接种', '无',       '喜欢户外运动，每天需要遛弯1小时以上，喜欢玩球', '张三', '13800001001', 1, '2025-01-15 10:00:00'),
(2, 1, '咪咪', '猫', '英国短毛猫', 2, 24,  4.20, '/pets/pet2.jpg', '健康', '猫三联已接种',               '对鸡肉过敏', '性格安静，喜欢晒太阳，不喜欢被陌生人抱',       '张三', '13800001001', 1, '2025-01-15 10:30:00'),
(3, 2, '旺财', '狗', '柯基犬',     1, 18, 12.00, '/pets/pet3.jpg', '健康', '狂犬疫苗、六联疫苗均已接种', '无',       '活泼好动，喜欢零食，需要控制体重',             '李四', '13800001002', 1, '2025-02-20 11:00:00'),
(4, 3, '小白', '猫', '布偶猫',     2, 12,  3.80, '/pets/pet4.jpg', '健康', '猫三联已接种',               '无',       '黏人，喜欢被抱着，胆子较小',                   '王五', '13800001003', 1, '2025-03-25 14:00:00');

-- -----------------------------------------------------------
-- 店铺数据: 2家店铺
-- -----------------------------------------------------------
INSERT INTO `shop` (`id`, `provider_id`, `name`, `description`, `logo`, `photos`, `province`, `city`, `district`, `address`, `longitude`, `latitude`, `phone`, `business_license`, `qualification`, `capacity`, `current_count`, `rating`, `status`, `verified`, `create_time`) VALUES
(1, 4, '阳光宠物之家', '专业宠物寄养服务，拥有独立空间、24小时监控、专业护理团队。提供猫狗寄养、日托及美容等全方位服务。', '/shops/logo1.jpg', '["/shops/shop1_1.jpg","/shops/shop1_2.jpg","/shops/shop1_3.jpg"]', '上海市', '上海市', '浦东新区', '张江高科技园区碧波路888号', 121.612290, 31.205830, '021-58001234', '/license/lic1.jpg', '/qual/qual1.jpg', 30, 5, 4.8, 1, 1, '2025-01-06 09:00:00'),
(2, 5, '萌宠乐园',     '温馨舒适的宠物寄养中心，环境优美，配备专业宠物医师，让您的爱宠享受度假般的体验。',              '/shops/logo2.jpg', '["/shops/shop2_1.jpg","/shops/shop2_2.jpg"]',                      '广东省', '广州市', '天河区',   '珠江新城花城大道168号',       113.324520, 23.119450, '020-38001234', '/license/lic2.jpg', '/qual/qual2.jpg', 20, 3, 4.6, 1, 1, '2025-01-09 10:00:00');

-- -----------------------------------------------------------
-- 服务项目数据: 6个服务
-- -----------------------------------------------------------
INSERT INTO `service_item` (`id`, `shop_id`, `name`, `description`, `species`, `price`, `original_price`, `unit`, `includes`, `photo`, `status`, `create_time`) VALUES
(1, 1, '狗狗标准寄养',   '标准独立房间，每日两餐、遛弯两次、日常清洁',             '狗', 128.00, 158.00, '天', '独立房间,每日两餐,遛弯两次,日常清洁,每日照片更新',       '/services/svc1.jpg', 1, '2025-01-07 09:00:00'),
(2, 1, '狗狗豪华寄养',   'VIP独立套房，每日三餐定制餐食、遛弯三次、SPA洗护',      '狗', 238.00, 288.00, '天', 'VIP套房,定制餐食,遛弯三次,SPA洗护,视频直播,每日报告', '/services/svc2.jpg', 1, '2025-01-07 09:30:00'),
(3, 1, '猫咪温馨寄养',   '安静独立猫舍，每日两餐、猫砂清理、陪玩',                 '猫',  98.00, 128.00, '天', '独立猫舍,每日两餐,猫砂清理,陪玩,每日照片更新',         '/services/svc3.jpg', 1, '2025-01-07 10:00:00'),
(4, 2, '狗狗快乐寄养',   '宽敞活动空间，专业护理团队，每日运动陪伴',               '狗', 118.00, 148.00, '天', '独立空间,每日两餐,户外运动,日常清洁,照片更新',         '/services/svc4.jpg', 1, '2025-01-10 09:00:00'),
(5, 2, '猫咪舒适寄养',   '静谧猫咪专区，温控环境，专业猫咪护理',                   '猫',  88.00, 108.00, '天', '独立猫位,每日两餐,猫砂清理,定时陪玩',                 '/services/svc5.jpg', 1, '2025-01-10 09:30:00'),
(6, 2, '小型犬日托',     '白天看护服务，适合上班族，含午餐和户外活动',             '狗',  68.00,  88.00, '天', '日间看护,午餐,户外活动,照片更新',                     '/services/svc6.jpg', 1, '2025-01-10 10:00:00');

-- -----------------------------------------------------------
-- 员工数据: 4名员工
-- -----------------------------------------------------------
INSERT INTO `employee` (`id`, `shop_id`, `name`, `phone`, `position`, `avatar`, `id_card`, `entry_date`, `salary`, `status`, `create_time`) VALUES
(1, 1, '周小红', '13700001001', '宠物护理师', '/avatars/emp1.jpg', '310101199601011111', '2025-01-10', 6000.00, 1, '2025-01-10 09:00:00'),
(2, 1, '吴大伟', '13700001002', '宠物护理师', '/avatars/emp2.jpg', '310101199702022222', '2025-01-10', 6000.00, 1, '2025-01-10 09:00:00'),
(3, 2, '郑美丽', '13700002001', '店长',       '/avatars/emp3.jpg', '440101199803033333', '2025-01-12', 8000.00, 1, '2025-01-12 09:00:00'),
(4, 2, '陈志强', '13700002002', '宠物护理师', '/avatars/emp4.jpg', '440101199904044444', '2025-01-15', 5500.00, 1, '2025-01-15 09:00:00');

-- -----------------------------------------------------------
-- 优惠券模板数据
-- -----------------------------------------------------------
INSERT INTO `coupon` (`id`, `shop_id`, `name`, `type`, `value`, `min_amount`, `total_count`, `remain_count`, `start_time`, `end_time`, `status`, `create_time`) VALUES
(1, 1, '新客立减50元', 1, 50.00, 200.00, 100, 85, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 1, '2025-01-07 10:00:00'),
(2, 1, '寄养8折券',    2,  8.00, 300.00,  50, 42, '2025-03-01 00:00:00', '2025-06-30 23:59:59', 1, '2025-03-01 09:00:00'),
(3, 2, '免费体验1天',  3,  1.00,   0.00,  30, 25, '2025-02-01 00:00:00', '2025-08-31 23:59:59', 1, '2025-02-01 10:00:00');

-- -----------------------------------------------------------
-- 订单数据: 3个订单
-- -----------------------------------------------------------
INSERT INTO `order_info` (`id`, `order_no`, `owner_id`, `shop_id`, `pet_id`, `service_id`, `start_date`, `end_date`, `days`, `price`, `total_amount`, `discount_amount`, `pay_amount`, `insurance`, `insurance_amount`, `coupon_id`, `status`, `remark`, `pay_time`, `accept_time`, `checkin_time`, `checkout_time`, `complete_time`, `create_time`) VALUES
(1, 'ORD20250315001', 1, 1, 1, 1, '2025-03-20', '2025-03-25', 5, 128.00, 640.00, 50.00, 600.00, 1, 10.00, 1, 6, '请注意豆豆每天需要遛弯', '2025-03-15 14:10:00', '2025-03-15 15:00:00', '2025-03-20 09:00:00', '2025-03-25 17:00:00', '2025-03-25 18:00:00', '2025-03-15 14:00:00'),
(2, 'ORD20250401001', 2, 1, 3, 2, '2025-04-05', '2025-04-10', 5, 238.00, 1190.00, 0.00, 1190.00, 1, 15.00, NULL, 4, '旺财比较活泼，多带它运动', '2025-04-01 10:10:00', '2025-04-01 11:00:00', '2025-04-05 10:00:00', NULL, NULL, '2025-04-01 10:00:00'),
(3, 'ORD20250410001', 3, 2, 4, 5, '2025-04-15', '2025-04-18', 3, 88.00, 264.00, 0.00, 264.00, 0, 0.00, NULL, 1, '小白比较胆小，请温柔对待', '2025-04-10 16:10:00', NULL, NULL, NULL, NULL, '2025-04-10 16:00:00');

-- -----------------------------------------------------------
-- 用户优惠券数据
-- -----------------------------------------------------------
INSERT INTO `user_coupon` (`id`, `user_id`, `coupon_id`, `order_id`, `status`, `receive_time`, `use_time`) VALUES
(1, 1, 1, 1,    1, '2025-03-10 10:00:00', '2025-03-15 14:00:00'),
(2, 2, 1, NULL, 0, '2025-03-12 11:00:00', NULL),
(3, 1, 2, NULL, 0, '2025-03-05 09:00:00', NULL),
(4, 3, 3, NULL, 0, '2025-04-01 15:00:00', NULL);

-- -----------------------------------------------------------
-- 照料记录数据
-- -----------------------------------------------------------
INSERT INTO `care_record` (`id`, `order_id`, `pet_id`, `shop_id`, `employee_id`, `type`, `content`, `photos`, `video`, `health_status`, `health_note`, `temperature`, `weight`, `create_time`) VALUES
(1, 1, 1, 1, 1, 1, '豆豆今天状态很好，适应了新环境，吃饭正常，精神很好。',                    '["/care/r1_1.jpg","/care/r1_2.jpg"]', NULL,           '良好', '一切正常',     38.5, 28.50, '2025-03-20 18:00:00'),
(2, 1, 1, 1, 1, 2, '早餐：狗粮200g+鸡胸肉50g，吃得很干净。',                                 '["/care/r2_1.jpg"]',                 NULL,           NULL,   NULL,           NULL, NULL,  '2025-03-20 08:30:00'),
(3, 1, 1, 1, 2, 3, '上午遛弯40分钟，在花园里跑步和玩球，很开心。',                           '["/care/r3_1.jpg","/care/r3_2.jpg"]', '/care/v3.mp4', NULL,   NULL,           NULL, NULL,  '2025-03-20 10:00:00'),
(4, 1, 1, 1, 1, 4, '完成日常清洁，更换垫子，清洗食盆水盆。',                                 '["/care/r4_1.jpg"]',                 NULL,           NULL,   NULL,           NULL, NULL,  '2025-03-20 14:00:00'),
(5, 1, 1, 1, 1, 5, '每日健康检查：体温正常，精神状态佳，食欲良好，大小便正常。',             NULL,                                 NULL,           '良好', '各项指标正常', 38.3, 28.40, '2025-03-21 09:00:00'),
(6, 1, 1, 1, 2, 1, '豆豆第二天表现很棒，与其他狗狗友好相处，晚上安静入睡。',                 '["/care/r6_1.jpg"]',                 NULL,           '良好', '适应良好',     38.4, 28.40, '2025-03-21 18:00:00'),
(7, 2, 3, 1, 1, 1, '旺财入住第一天，比较兴奋，四处探索，吃饭正常。',                         '["/care/r7_1.jpg","/care/r7_2.jpg"]', NULL,           '良好', '精力充沛',     38.6, 12.00, '2025-04-05 18:00:00'),
(8, 2, 3, 1, 2, 3, '上午带旺财户外运动1小时，消耗了不少精力，回来后安静了很多。',             '["/care/r8_1.jpg"]',                 '/care/v8.mp4', NULL,   NULL,           NULL, NULL,  '2025-04-06 10:30:00');

-- -----------------------------------------------------------
-- 评价数据
-- -----------------------------------------------------------
INSERT INTO `review` (`id`, `order_id`, `owner_id`, `shop_id`, `rating`, `content`, `photos`, `reply`, `reply_time`, `status`, `create_time`) VALUES
(1, 1, 1, 1, 5, '非常满意！店家照顾得很好，每天都会发照片和视频，豆豆回来还胖了一点哈哈。环境干净整洁，工作人员也很专业，下次还会选择这家！', '["/reviews/rev1_1.jpg","/reviews/rev1_2.jpg"]', '感谢您的认可！豆豆很乖很可爱，欢迎下次再来哦~', '2025-03-26 10:00:00', 1, '2025-03-25 20:00:00');

-- -----------------------------------------------------------
-- 投诉数据
-- -----------------------------------------------------------
INSERT INTO `complaint` (`id`, `order_id`, `complainant_id`, `shop_id`, `type`, `content`, `photos`, `status`, `reply`, `reply_time`, `result`, `create_time`) VALUES
(1, 1, 1, 1, 4, '寄养期间有一天没有收到照片更新，希望能改善。', NULL, 2, '非常抱歉，当天护理师临时换班导致遗漏，我们已加强管理流程，感谢您的反馈。', '2025-03-27 14:00:00', '已优化内部流程，赠送一张50元优惠券作为补偿', '2025-03-26 09:00:00');

-- -----------------------------------------------------------
-- 考勤数据
-- -----------------------------------------------------------
INSERT INTO `attendance` (`id`, `employee_id`, `shop_id`, `date`, `clock_in`, `clock_out`, `status`, `remark`, `create_time`) VALUES
(1, 1, 1, '2025-04-05', '2025-04-05 08:55:00', '2025-04-05 18:05:00', 1, NULL,       '2025-04-05 08:55:00'),
(2, 2, 1, '2025-04-05', '2025-04-05 09:10:00', '2025-04-05 18:00:00', 2, '地铁延误', '2025-04-05 09:10:00'),
(3, 1, 1, '2025-04-06', '2025-04-06 08:50:00', '2025-04-06 18:00:00', 1, NULL,       '2025-04-06 08:50:00'),
(4, 2, 1, '2025-04-06', '2025-04-06 08:58:00', '2025-04-06 18:02:00', 1, NULL,       '2025-04-06 08:58:00'),
(5, 3, 2, '2025-04-05', '2025-04-05 08:45:00', '2025-04-05 18:30:00', 1, NULL,       '2025-04-05 08:45:00'),
(6, 4, 2, '2025-04-05', NULL,                   NULL,                  5, '事假一天', '2025-04-05 09:00:00'),
(7, 3, 2, '2025-04-06', '2025-04-06 08:50:00', '2025-04-06 17:30:00', 3, '提前下班', '2025-04-06 08:50:00'),
(8, 4, 2, '2025-04-06', '2025-04-06 09:00:00', '2025-04-06 18:00:00', 1, NULL,       '2025-04-06 09:00:00');

-- -----------------------------------------------------------
-- 积分记录数据
-- -----------------------------------------------------------
INSERT INTO `points_record` (`id`, `user_id`, `type`, `points`, `description`, `order_id`, `create_time`) VALUES
(1, 1, 1,  100, '注册赠送积分',     NULL, '2025-01-10 08:00:00'),
(2, 1, 1,  120, '订单完成奖励积分', 1,    '2025-03-25 18:00:00'),
(3, 1, 1,   50, '评价奖励积分',     1,    '2025-03-25 20:00:00'),
(4, 1, 2,  -10, '积分兑换优惠券',   NULL, '2025-03-28 10:00:00'),
(5, 2, 1,  100, '注册赠送积分',     NULL, '2025-02-15 09:30:00'),
(6, 2, 1,   50, '首次下单奖励',     2,    '2025-04-01 10:00:00'),
(7, 3, 1,   50, '注册赠送积分',     NULL, '2025-03-20 10:00:00');

-- -----------------------------------------------------------
-- 消息通知数据
-- -----------------------------------------------------------
INSERT INTO `message` (`id`, `sender_id`, `receiver_id`, `order_id`, `type`, `title`, `content`, `is_read`, `create_time`) VALUES
(1,  NULL, 1, NULL, 1, '欢迎注册',       '欢迎您注册宠物寄养平台！我们将为您和您的爱宠提供最优质的寄养服务。',                        1, '2025-01-10 08:00:00'),
(2,  NULL, 1, 1,    2, '订单已接单',     '您的订单 ORD20250315001 已被阳光宠物之家接单，请按时送达宠物。',                             1, '2025-03-15 15:00:00'),
(3,  NULL, 1, 1,    2, '宠物已入住',     '您的爱宠豆豆已顺利入住阳光宠物之家，请放心。',                                             1, '2025-03-20 09:00:00'),
(4,  1,    4, 1,    3, '今日照料报告',   '豆豆今天状态很好，吃饭正常，运动充足，请查看详细照料记录。',                                 1, '2025-03-20 18:00:00'),
(5,  NULL, 1, 1,    2, '订单已完成',     '您的订单 ORD20250315001 已完成，感谢您的信任，期待您的评价！',                               1, '2025-03-25 18:00:00'),
(6,  NULL, 2, 2,    2, '订单已接单',     '您的订单 ORD20250401001 已被阳光宠物之家接单，请于2025-04-05按时送达宠物。',                 1, '2025-04-01 11:00:00'),
(7,  NULL, 2, 2,    2, '宠物已入住',     '您的爱宠旺财已顺利入住阳光宠物之家，我们会精心照料。',                                       1, '2025-04-05 10:00:00'),
(8,  NULL, 2, 2,    3, '今日照料报告',   '旺财入住第一天，比较兴奋，吃饭正常，请查看照料记录。',                                       0, '2025-04-05 18:00:00'),
(9,  NULL, 3, 3,    2, '订单待接单',     '您的订单 ORD20250410001 已支付成功，等待萌宠乐园确认接单。',                                 0, '2025-04-10 16:10:00'),
(10, NULL, 5, 3,    2, '新订单通知',     '您有一个新的寄养订单 ORD20250410001，请尽快确认。',                                          0, '2025-04-10 16:10:00');

SET FOREIGN_KEY_CHECKS = 1;
