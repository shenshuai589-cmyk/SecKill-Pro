package com.seckillpro.config;


import com.seckillpro.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * Spring MVC提供的配置接口，
     * 实现它可以自定义很多MVC相关的行为，
     * 这里我们只用到"注册拦截器"这一个功能
     */

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry  registry) {
        /**
         * registry.addInterceptor(jwtInterceptor)：把 JwtInterceptor 注册到Spring MVC里，告诉框架"这个拦截器要生效"
         */
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**") // 拦截所有/api开头的接口
                .excludePathPatterns(
                        "/api/auth/register",  // 注册不需要登录
                        "/api/auth/login",   // 登录不需要登录
                        "/api/seckill/goods/list",  // 商品列表，游客也能看
                        "/api/seckill/goods/*"   // 商品详情，游客也能看
                );
    }
}
