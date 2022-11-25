package com.example.onlinestore.controller;

import com.example.onlinestore.common.ApiRestResponse;
import com.example.onlinestore.common.Constant;
import com.example.onlinestore.exception.OnlineStore_Exception;
import com.example.onlinestore.exception.OnlineStore_ExceptionEnum;
import com.example.onlinestore.model.pojo.User;
import com.example.onlinestore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/test")
    @ResponseBody
    public User personalPage(){
        return userService.getUser ();
    }

    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("userName") String userName,@RequestParam("password") String password) throws OnlineStore_Exception, NoSuchAlgorithmException {
        //用户名位空
        if (StringUtils.isEmpty (userName)){
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.NEED_USER_NAME);
        }
        //密码为空
        if (StringUtils.isEmpty (password)){
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.NEED_PASSWORD);
        }
        //密码长度不能小于8位
        if (password.length ()<8){
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register (userName,password);
        return ApiRestResponse.success ();
    }

    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession httpSession) throws OnlineStore_Exception {
        if (StringUtils.isEmpty (userName)){
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.NEED_USER_NAME);
        }
        //密码为空
        if (StringUtils.isEmpty (password)){
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.NEED_PASSWORD);
        }
        //密码长度不能小于8位
        if (password.length ()<8){
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.PASSWORD_TOO_SHORT);
        }
        User user = userService.login (userName, password);
        //密码不传给前端
        user.setPassword (null);
        httpSession.setAttribute (Constant.ONLINESTORE_USER,user);
        return ApiRestResponse.success ( user );
    }

    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse userUpdate(HttpSession httpSession,@RequestParam String signature) throws OnlineStore_Exception {
        User curry_user = (User)httpSession.getAttribute (Constant.ONLINESTORE_USER);
        if (curry_user==null){
            throw new OnlineStore_Exception(OnlineStore_ExceptionEnum.NEED_LOGIN);
        }
        User user = new User ();
        user.setId (curry_user.getId ());
        user.setpersonalized_signature (signature);
        userService.updateInformation (user);
        return ApiRestResponse.success ();
    }

    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession httpSession){
        httpSession.removeAttribute (Constant.ONLINESTORE_USER);
        return ApiRestResponse.success ();
    }

    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse AdminLogin(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession httpSession) throws OnlineStore_Exception {
        if (StringUtils.isEmpty (userName)){
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.NEED_USER_NAME);
        }
        //密码为空
        if (StringUtils.isEmpty (password)){
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.NEED_PASSWORD);
        }
        //密码长度不能小于8位
        if (password.length ()<8){
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.PASSWORD_TOO_SHORT);
        }
        User user = userService.login (userName, password);
        if (userService.checkAdminRole (user)) {
            user.setPassword (null);
            httpSession.setAttribute (Constant.ONLINESTORE_USER,user);
            return ApiRestResponse.success ( user );
        }else {
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.NEED_ADMIN);
        }
    }
}
