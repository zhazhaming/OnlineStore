package com.example.onlinestore.common;

import com.example.onlinestore.exception.OnlineStore_Exception;
import com.example.onlinestore.exception.OnlineStore_ExceptionEnum;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class Constant {
    //md5加密盐值
    public static final String SALT = "8svbsvjkweDf,.03[";
    // session 中存user的名称
    public static final String ONLINESTORE_USER = "onlinestore_user";

    // 文件上传地址
    public static String FILER_UPLOAD_DIR;
    @Value ("${file.upload.dir}")
    public void setFilerUploadDir(String filerUploadDir){
        FILER_UPLOAD_DIR = filerUploadDir;
    }

    // 商品列表使用降序或者是升序
    public  interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet ( "price desc" ,"price asc" );
    }

    // 商品上下架状态
    public interface SaleStatus{
        int NOT_SALE = 0;  //商品下架
        int SALE = 1;  //商品上架
    }

    // 购物车商品是否选中状态
    public interface CartStatus{
        int NOT_CHECKED = 0;  //购物车商品未选中
        int CHECKED = 1;  //购物车商品选中
    }

    public enum OrderStatusEnum{
        CANCELED(0,"用户已取消"),
        NOT_PAID(10,"未付款"),
        PAID(20,"已付款"),
        DELIVERED(30,"已发货"),
        FINISHED(40,"交易完成");
        private String value;
        private int code;

        OrderStatusEnum( int code,String value) {
            this.value = value;
            this.code = code;
        }

        public static OrderStatusEnum codeOf(Integer code){
            for (OrderStatusEnum orderStatusEnum:values ()){
                if (orderStatusEnum.getCode ()==code){
                    return orderStatusEnum;
                }
            }
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NO_ENUM);
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
