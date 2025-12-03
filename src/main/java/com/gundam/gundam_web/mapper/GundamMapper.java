package com.gundam.gundam_web.mapper;

import com.gundam.gundam_web.entity.ForumPost;
import com.gundam.gundam_web.entity.Gundam;
import com.gundam.gundam_web.entity.User;
import org.apache.ibatis.annotations.Delete; // 记得引入这个
import org.apache.ibatis.annotations.Insert; // 还有这个
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface GundamMapper {
    // 1. 查询所有
    @Select("SELECT * FROM gundam")
    List<Gundam> findAll();

    // 2. 按等级筛选
    @Select("SELECT * FROM gundam WHERE grade = #{grade}")
    List<Gundam> findByGrade(String grade);

    // 3. 查单个详情
    @Select("SELECT * FROM gundam WHERE id = #{id}")
    Gundam findById(Integer id);

    // 4. 用户注册
    @Insert("INSERT INTO user(username, password, email) VALUES(#{username}, #{password}, #{email})")
    void insertUser(User user);

    // 5. 用户登录
    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password}")
    User login(String username, String password);
    @Select("SELECT * FROM forum_post ORDER BY post_time DESC")
    List<ForumPost> findAllPosts();

// 7. 插入新帖子
    @Insert("INSERT INTO forum_post(callsign, message) VALUES(#{callsign}, #{message})")
    void insertPost(ForumPost post);

    // 8. 插入新机体 
    // 【重要修改】方法名改为 insert，以匹配 Controller 的调用
    @Insert("INSERT INTO gundam(name, series, grade, price, height, weight, pilot, image_url, description) " +
            "VALUES(#{name}, #{series}, #{grade}, #{price}, #{height}, #{weight}, #{pilot}, #{imageUrl}, #{description})")
    void insert(Gundam gundam);

    // 9. 删除机体
    @Delete("DELETE FROM gundam WHERE id = #{id}")
    void deleteById(Integer id);
    
}