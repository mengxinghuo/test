package com.test.dao;

import com.test.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * login
     * 1.校验用户名是否存在
     * 2.校检用户名和密码的正确性
     */
    int checkUsername(String username);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    /**
     * 注册
     * 1.校检邮箱
     */
    int checkEmail(String email);

    /**
     * 获取用户信息
     * 1.根据用户名查询密码问题
     *
     * @param username
     * @return
     */
    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updatePasswordByUsername(@Param("username") String username, @Param("password") String passwordNew);

    int checkPassword(@Param("password")String password,@Param("userId")Integer userId);

    int checkEmailByUserId(@Param("email") String email,@Param("userId") Integer userId);
}

