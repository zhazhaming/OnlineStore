package com.example.onlinestore.service.impl;

import com.example.onlinestore.common.ApiRestResponse;
import com.example.onlinestore.exception.OnlineStore_Exception;
import com.example.onlinestore.exception.OnlineStore_ExceptionEnum;
import com.example.onlinestore.model.dao.UserMapper;
import com.example.onlinestore.model.pojo.User;
import com.example.onlinestore.service.UserService;
import com.example.onlinestore.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;


@Service
public class UserSerciveImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey (1);
    }

    @Override
    public void register(String userName,String password) throws OnlineStore_Exception, NoSuchAlgorithmException {
        User user = userMapper.selectByName (userName);
        if (user!=null){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NAME_EXISTED);
        }
        User user1 = new User ();
        user1.setUsername (userName);
        user1.setPassword (MD5Utils.getMD5Str(password));
        int count = userMapper.insertSelective (user1);
        if (count==0){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public User login(String userName, String password) throws OnlineStore_Exception {
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMD5Str (password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace ( );
        }
        User user = userMapper.selectLogin (userName, md5Password);
        if (user==null){
            throw new OnlineStore_Exception(OnlineStore_ExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    @Override
    public void updateInformation(User user) throws OnlineStore_Exception {
        int count = userMapper.updateByPrimaryKeySelective (user);
        if (count>1){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.UPDATE_FAILE);
        }
    }

    @Override
    public boolean checkAdminRole(User user){
        return user.getRole ().equals (2);
    }
}
