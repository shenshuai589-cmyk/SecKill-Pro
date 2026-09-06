-- KEYS[1]：库存的key，比如 seckill:stock:2
-- ARGV[1]：用户id
-- KEYS[2]：记录已秒杀用户的key，比如 seckill:users:2



local stockKey = KEYS[1]
local userKey = KEYS[2]
local userId = ARGV[1]

-- 1. 判断这个用户是否已经参与过秒杀

if redis.call('sismember', userKey, userId) == 1 then
    return 2 -- 用户已经参与过秒杀
end

-- 2. 判断库存是否充足
local stock = tonumber(redis.call('get', stockKey))

-- 3.库存不足
if stock <= 0 then
    return -1 -- 库存不足
end

-- 4.库存充足，扣减库存
redis.call('decr', stockKey)

-- 5.记录用户参与秒杀的行为
redis.call('sadd', userKey, userId)

return 1 -- 秒杀成功