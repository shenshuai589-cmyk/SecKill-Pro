-- 秒杀商品表
CREATE TABLE `seckill_goods` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `goods_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
  `goods_img` VARCHAR(255) COMMENT '商品图片',
  `goods_detail` VARCHAR(500) COMMENT '商品描述',
  `original_price` DECIMAL(10,2) NOT NULL COMMENT '原价',
  `seckill_price` DECIMAL(10,2) NOT NULL COMMENT '秒杀价',
  `stock` INT NOT NULL COMMENT '总库存',
  `stock_count` INT NOT NULL COMMENT '剩余库存',
  `start_time` DATETIME NOT NULL COMMENT '秒杀开始时间',
  `end_time` DATETIME NOT NULL COMMENT '秒杀结束时间',
  `status` TINYINT DEFAULT 0 COMMENT '0-未开始 1-进行中 2-已结束 3-已下架',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_status_time (`status`, `start_time`, `end_time`)
) ENGINE=InnoDB COMMENT='秒杀商品表';

-- 秒杀订单表
CREATE TABLE `seckill_order` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
  `user_id` BIGINT NOT NULL,
  `goods_id` BIGINT NOT NULL,
  `goods_name` VARCHAR(100) NOT NULL COMMENT '冗余字段，避免商品被删后无法追溯',
  `seckill_price` DECIMAL(10,2) NOT NULL,
  `order_status` TINYINT DEFAULT 0 COMMENT '0-待支付 1-已支付 2-已取消 3-已超时关闭',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `pay_time` DATETIME COMMENT '支付时间',
  UNIQUE KEY uk_user_goods (`user_id`, `goods_id`) COMMENT '防止用户对同一商品重复下单',
  INDEX idx_goods_id (`goods_id`)
) ENGINE=InnoDB COMMENT='秒杀订单表';

-- 用户表（可直接复用校园易物的user表）
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `nickname` VARCHAR(50),
  `avatar` VARCHAR(255),
  `role` VARCHAR(20) DEFAULT 'USER' COMMENT 'USER-普通用户 ADMIN-管理员',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='用户表';

-- 秒杀日志表（可选，用于压测/分析时统计每次请求）
CREATE TABLE `seckill_log` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `goods_id` BIGINT NOT NULL,
  `result` TINYINT COMMENT '0-失败(售罄) 1-成功 2-重复请求拦截',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='秒杀请求日志表';