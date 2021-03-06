package com.test.service.impl;

import com.test.common.Const;
import com.test.common.ServerResponse;
import com.test.common.TokenCache;
import com.test.dao.UserMapper;
import com.test.pojo.User;
import com.test.service.IUserService;
import com.test.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMassage("用户名不存在");
        }

        //todo 密码登录MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMassage("密码错误");
        }
        //?为什么登录成功，密码要设置为空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);

    }

    public ServerResponse<String> register(User user) {

        ServerResponse vaildResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!vaildResponse.isSuccess()) {
            return vaildResponse;
        }
        vaildResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!vaildResponse.isSuccess()) {
            return vaildResponse;
        }


        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMassage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            //开始校验
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMassage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMassage("email已存在");
                }
            }

        } else {
            return ServerResponse.createByErrorMassage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }


    public ServerResponse selectQuestion(String username) {
        ServerResponse vaildResponse = this.checkValid(username, Const.USERNAME);
        if (vaildResponse.isSuccess()) {

            return ServerResponse.createByErrorMassage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMassage("找回密码的问题是空的");
    }

    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            //说明问题及问题答案是这个用户的，并且是正确的
            String foregtToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, foregtToken);
            return ServerResponse.createBySuccess(foregtToken);
        }
        return ServerResponse.createByErrorMassage("问题的答案错误");
    }


    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMassage("参数错误，token需要传递");
        }
        ServerResponse vaildResponse = this.checkValid(username, Const.USERNAME);
        if (vaildResponse.isSuccess()) {

            return ServerResponse.createByErrorMassage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMassage("token无效或者过期");
        }
        if (StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);

            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorMassage("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMassage("修改密码失败");
    }


    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMassage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));

        int updateCount = userMapper.updateByPrimaryKeySelective(user);

        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMassage("密码更新失败");
    }


    public ServerResponse<User> updateInformation(User user) {
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMassage("email已存在，请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByErrorMassage("更新个人信息失败");
    }


    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMassage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


    //校验是否是管理员
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        } else {
            return ServerResponse.createByError();
        }
    }
}
