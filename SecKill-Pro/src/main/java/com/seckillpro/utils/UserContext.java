package com.seckillpro.utils;

public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();  // 用来存储用户id

    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();  // 用来存储角色


    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void setRole(String role) {
        ROLE.set(role);
    }

    public static String getRole() {
        return ROLE.get();
    }

    public static void clear() {
        //
        USER_ID.remove();
        ROLE.remove();
    }
}
