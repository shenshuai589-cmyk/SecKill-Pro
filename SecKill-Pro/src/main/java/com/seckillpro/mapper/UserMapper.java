package com.seckillpro.mapper;

import com.seckillpro.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    // 添加用户
    int insert(User user);

    // 根据用户名查询用户
    User selectByUsername(@Param("username") String username);

    // 根据id查询用户信息
    User selectById(@Param("id") int id);
}
