package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 根据openid来查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByUserId(String openid);

    /**
     * 插入数据，要获取主键值的
     * @param user
     */
    void insert(User user);

    /**
     * 根据用户id查询用户
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{id}")
    User getById(Long userId);
}
