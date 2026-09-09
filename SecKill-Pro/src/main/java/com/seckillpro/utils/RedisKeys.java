package com.seckillpro.utils;

/**
 * Redis key 前缀统一管理，避免生产者/消费者两端字符串拼写不一致导致读不到数据
 */

public class RedisKeys {

    public static final String STOCK_KEY_PREFIX = "seckill:stock:";

    public static final String USERS_KEY_PREFIX = "seckill:users:";

    // 秒杀结果状态：seckill:result:{goodsId}:{userId} -> {status, orderNo, orderId}
    public static final String RESULT_KEY_PREFIX = "seckill:result:";

    public static String resultKey(Long goodsId, Long userId){
        return RESULT_KEY_PREFIX + goodsId +":"+ userId;
    }
}
