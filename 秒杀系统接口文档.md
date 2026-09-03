# 秒杀系统（SecKill-Pro）接口文档

**版本**：v1.0
**基础路径**：`/api`
**认证方式**：JWT，除登录/注册外，所有接口需在请求头携带 `Authorization: Bearer {token}`

---

## 一、统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**通用状态码**

| code | 说明 |
|---|---|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录/token失效 |
| 403 | 无权限（如普通用户访问管理端接口） |
| 409 | 业务冲突（如重复下单、库存不足） |
| 500 | 服务器内部错误 |

---

## 二、用户认证接口

### 2.1 用户注册

`POST /api/auth/register`

**请求体**
```json
{
  "username": "zhangsan",
  "password": "123456",
  "nickname": "张三"
}
```

**响应**
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

### 2.2 用户登录

`POST /api/auth/login`

**请求体**
```json
{
  "username": "zhangsan",
  "password": "123456"
}
```

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1001,
    "nickname": "张三",
    "role": "USER"
  }
}
```

---

## 三、用户端 —— 秒杀商品接口

### 3.1 秒杀商品列表

`GET /api/seckill/goods/list`

**请求参数（Query）**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| pageNum | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页数量，默认10 |
| status | int | 否 | 0-未开始 1-进行中 2-已结束，不传则查全部 |

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 25,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "goodsName": "iPhone 16 Pro",
        "goodsImg": "https://xxx/img.jpg",
        "originalPrice": 8999.00,
        "seckillPrice": 6999.00,
        "stockCount": 50,
        "startTime": "2026-09-05 10:00:00",
        "endTime": "2026-09-05 11:00:00",
        "status": 1
      }
    ]
  }
}
```

### 3.2 秒杀商品详情

`GET /api/seckill/goods/{id}`

**路径参数**：`id` — 商品ID

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "goodsName": "iPhone 16 Pro",
    "goodsImg": "https://xxx/img.jpg",
    "goodsDetail": "商品详情描述",
    "originalPrice": 8999.00,
    "seckillPrice": 6999.00,
    "stockCount": 48,
    "startTime": "2026-09-05 10:00:00",
    "endTime": "2026-09-05 11:00:00",
    "status": 1,
    "hasParticipated": false
  }
}
```
> `stockCount` 实时读取 Redis，而非数据库，保证前端展示的库存是最新的。

### 3.3 发起秒杀（核心接口）

`POST /api/seckill/{goodsId}`

**路径参数**：`goodsId` — 商品ID

**请求头**：需携带 JWT

**响应 — 成功进入排队**
```json
{
  "code": 200,
  "message": "排队中，请稍后查看订单结果",
  "data": {
    "queueStatus": "PENDING"
  }
}
```

**响应 — 已售罄**
```json
{
  "code": 409,
  "message": "该商品已售罄",
  "data": null
}
```

**响应 — 重复参与**
```json
{
  "code": 409,
  "message": "您已参与过本次秒杀，请勿重复提交",
  "data": null
}
```

**响应 — 未在秒杀时间内**
```json
{
  "code": 400,
  "message": "秒杀尚未开始或已结束",
  "data": null
}
```

### 3.4 查询秒杀结果

`GET /api/seckill/result/{goodsId}`

> 前端下单后轮询此接口（建议间隔1~2秒，最多轮询10次），获取MQ异步处理后的最终结果。

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "status": "SUCCESS",
    "orderNo": "SK202609031234567",
    "orderId": 8888
  }
}
```

**status 枚举**：`PENDING`（处理中）/ `SUCCESS`（成功）/ `FAILED`（失败，库存不足）

---

## 四、用户端 —— 订单接口

### 4.1 我的订单列表

`GET /api/order/my`

**请求参数（Query）**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| pageNum | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页数量，默认10 |
| orderStatus | int | 否 | 0-待支付 1-已支付 2-已取消 3-已超时关闭 |

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 3,
    "list": [
      {
        "orderNo": "SK202609031234567",
        "goodsName": "iPhone 16 Pro",
        "seckillPrice": 6999.00,
        "orderStatus": 0,
        "createTime": "2026-09-03 10:00:15"
      }
    ]
  }
}
```

### 4.2 订单详情

`GET /api/order/{orderNo}`

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderNo": "SK202609031234567",
    "goodsName": "iPhone 16 Pro",
    "seckillPrice": 6999.00,
    "orderStatus": 0,
    "createTime": "2026-09-03 10:00:15",
    "payTime": null
  }
}
```

---

## 五、管理端接口（需 ADMIN 角色）

### 5.1 发布秒杀商品

`POST /api/admin/seckill/goods`

**请求体**
```json
{
  "goodsName": "iPhone 16 Pro",
  "goodsImg": "https://xxx/img.jpg",
  "goodsDetail": "商品详情描述",
  "originalPrice": 8999.00,
  "seckillPrice": 6999.00,
  "stock": 50,
  "startTime": "2026-09-05 10:00:00",
  "endTime": "2026-09-05 11:00:00"
}
```

**响应**
```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "id": 1
  }
}
```
> 发布成功后，服务端需同步执行 `SET seckill:stock:{goodsId} {stock}`，初始化 Redis 库存。

### 5.2 编辑秒杀商品

`PUT /api/admin/seckill/goods/{id}`

请求体同 5.1，字段均可选（部分更新）。

### 5.3 下架秒杀商品

`DELETE /api/admin/seckill/goods/{id}`

**响应**
```json
{
  "code": 200,
  "message": "下架成功",
  "data": null
}
```

### 5.4 管理端订单列表

`GET /api/admin/seckill/orders`

**请求参数（Query）**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| pageNum | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页数量，默认10 |
| orderStatus | int | 否 | 订单状态筛选 |
| goodsId | long | 否 | 按商品筛选 |
| startTime / endTime | string | 否 | 按下单时间区间筛选 |

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 48,
    "list": [
      {
        "orderNo": "SK202609031234567",
        "userId": 1001,
        "nickname": "张三",
        "goodsName": "iPhone 16 Pro",
        "seckillPrice": 6999.00,
        "orderStatus": 1,
        "createTime": "2026-09-03 10:00:15"
      }
    ]
  }
}
```

### 5.5 库存监控

`GET /api/admin/seckill/stock/{goodsId}`

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "goodsId": 1,
    "redisStock": 12,
    "mysqlStock": 12,
    "totalStock": 50,
    "soldCount": 38,
    "isConsistent": true
  }
}
```
> `isConsistent` 用于判断 Redis 与 MySQL 库存是否一致，方便排查数据不一致问题（这是秒杀系统面试常问的点，值得在此接口上多花心思）。

---

## 六、错误码汇总

| code | message | 触发场景 |
|---|---|---|
| 400 | 参数错误 | 请求体校验失败 |
| 400 | 秒杀尚未开始或已结束 | 不在活动时间窗口内下单 |
| 401 | 登录已过期，请重新登录 | JWT失效/未携带 |
| 403 | 无权限访问 | 普通用户调用管理端接口 |
| 409 | 该商品已售罄 | Redis库存扣减为负 |
| 409 | 您已参与过本次秒杀 | Redis Set 判重命中 |
| 500 | 系统繁忙，请稍后重试 | 未捕获异常 |

---

## 七、关键设计说明（面试可重点讲的部分）

1. **发起秒杀接口是"快速响应+异步处理"模式**：请求先做 Redis 库存预扣 + 用户去重，通过后立即返回"排队中"，真正的下单落库交给 MQ 消费者异步完成，避免高并发直接打爆数据库。
2. **库存双重校验**：Redis 预扣是第一道防线，MQ 消费者写库前再查一次 MySQL 库存是第二道防线，防止 Redis 与 MySQL 数据不一致导致超卖。
3. **防重复下单**采用 Redis Set（`seckill:users:{goodsId}`）而不是直接查数据库唯一索引，减少数据库压力，唯一索引作为兜底保障。
4. **前端轮询设计**：由于下单是异步的，前端需要通过 `/api/seckill/result/{goodsId}` 轮询获取最终结果，这个设计也是面试时容易被追问"为什么不用WebSocket推送"的点，可以提前想好回答（用轮询是为了降低系统复杂度，秒杀场景下单次轮询周期短，可接受）。
