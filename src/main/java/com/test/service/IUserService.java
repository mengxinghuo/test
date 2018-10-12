package com.test.service;

import com.test.common.ServerResponse;
import com.test.pojo.User;

public interface IUserService {
    //登录
    ServerResponse<User> login(String username, String password);

    //注册
    ServerResponse<String> register(User user);

    //校验参数
    ServerResponse<String> checkValid(String str, String type);

    //根据用户名查询密码问题
    ServerResponse selectQuestion(String username);

    //根据用户名校检问题和答案
    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse<String>resetPassword( String passwordOld, String passwordNew,User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User>getInformation(Integer userId);



    ServerResponse checkAdminRole(User user);

}
