package com.seckillpro.interceptor;


import com.seckillpro.annotation.RequireRole;
import com.seckillpro.utils.JwtUtil;
import com.seckillpro.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {  // 实现Spring MVC提供的"拦截器"接口,就自动具备了"拦截请求"的能力

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception { // preHandle,在请求到达Controller之前
        String authHeader = request.getHeader("Authorization");

        /**
         * 从请求头里取出 Authorization 这个字段（前端发请求时，会把token放在这个请求头里，格式是 Bearer eyJhbGc...）
         * 如果这个字段根本不存在，或者格式不对（没有以Bearer 开头），
         * 说明这个请求压根没带token，直接返回401（未授权），
         * 并且 return false 拦截掉，不往下走了
         */

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeErrorResponse(response,401,"未登录或token缺失");
            return false;  // 返回 false → 拦截，请求到此为止，不会执行Controller里的代码
        }


        String token = authHeader.substring(7);

        /**
         * Authorization 字段的值长这样："Bearer eyJhbGciOiJIUzI1NiJ9..."，
         * 前面7个字符是 "Bearer "（包括一个空格），
         * substring(7) 就是去掉这7个字符，只保留真正的token部分。
         */
        Long userId;
        String role;

        try{
            Claims claims = jwtUtil.parseToken(token);
             userId = claims.get("userId", Long.class);
             role = claims.get("role", String.class);

            UserContext.setUserId(userId);
            UserContext.setRole(role);

            /**
             * 调用之前写的 JwtUtil.parseToken() 去验证并解析这个token
             * 如果token是伪造的、或者已经过期，parseToken 内部会抛异常，被 catch 捕获，返回401拦截掉
             * 如果解析成功，说明这是个合法的、没过期的token，从里面取出 userId 和 role，
             * 存进 UserContext（上一条消息详细讲过的"线程专属储物柜"），然后 return true 放行
             */

            if (handler instanceof HandlerMethod handlerMethod) {
                RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
                if (requireRole != null && !requireRole.value().equals(role)) {
                    writeErrorResponse(response,403,"无权限访问该接口");
                    return false;
                }
            }

            return true; // 返回 true → 放行，请求继续往Controller走
        } catch (Exception e) {
            writeErrorResponse(response,401,"token无效或已过期");
            return false; // 返回 false → 拦截，请求到此为止，不会执行Controller里的代码
        }

        //  检查这个接口方法上有没有 @RequireRole 注解，如果有，校验角色是否匹配

    }


    @Override
    public void afterCompletion(HttpServletRequest request,  // afterCompletion,，在整个请求处理完毕、即将返回给用户之前
                                HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

        UserContext.clear();
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(String.format("{\"code\":%d,\"message\":\"%s\",\"data\":null}", status, message));
    }
}
