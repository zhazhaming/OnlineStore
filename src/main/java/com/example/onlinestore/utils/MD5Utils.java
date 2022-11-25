package com.example.onlinestore.utils;

import com.example.onlinestore.common.Constant;
import org.apache.tomcat.util.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * MD5进行加密
 */
public class MD5Utils {
    public static String getMD5Str(String strValue) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance ("MD5");
        return Base64.encodeBase64String (md5.digest ((strValue+ Constant.SALT).getBytes(StandardCharsets.UTF_8)));
    }

    //md5测试
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String password = "123456789";
        String md5Str = MD5Utils.getMD5Str (password);
        System.out.println (md5Str);
    }
}
