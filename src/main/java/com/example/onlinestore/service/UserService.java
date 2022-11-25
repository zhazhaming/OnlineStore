package com.example.onlinestore.service;

import com.example.onlinestore.exception.OnlineStore_Exception;
import com.example.onlinestore.model.pojo.User;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;


public interface UserService {
     User getUser();

     void register(String userName,String password) throws OnlineStore_Exception, NoSuchAlgorithmException;

    User login(String userName, String password) throws OnlineStore_Exception;

    void updateInformation(User user) throws OnlineStore_Exception;

    boolean checkAdminRole(User user);
}
