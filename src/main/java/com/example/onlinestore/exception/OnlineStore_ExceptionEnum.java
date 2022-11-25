package com.example.onlinestore.exception;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 * 异常枚举
 */
public enum OnlineStore_ExceptionEnum {
    NEED_USER_NAME(10001,"用户名不能为空"),
    NEED_PASSWORD(10002,"密码不能为空"),
    PASSWORD_TOO_SHORT(1003,"密码长度不能小于8位"),
    NAME_EXISTED(1004,"不允许重名"),
    INSERT_FAILED(1005,"插入失败，请重试"),
    WRONG_PASSWORD(1006,"用户名或密码错误"),
    NEED_LOGIN(1007,"用户未登录"),
    UPDATE_FAILE(1008,"更新失败"),
    NEED_ADMIN(1009,"无管理员权限"),
    PARA_NOT_NULL(10010,"参数不能为空"),
    CREATE_FAILE(10011,"新增失败"),
    REQUEST_PARAM_ERROR(10012,"参数错误"),
    DELETE_FAILE(10013,"删除失败"),
    MKDIR_FAILE(10014,"文件夹创建失败"),
    UPLOAD_FAILE(10015,"图片上传失败"),
    NOT_SALE(10016,"商品不可售"),
    NOT_ENOUGH(10017,"商品库存不足"),
    CART_EMPTY(10018,"勾选要购买的商品"),
    NO_ENUM(10019,"未找到对应的枚举类"),
    NO_ORDER(10020,"未查询到订单"),
    NO_YOUR_ORDER(10021,"不是你的订单"),
    WRONG_ORDER(10022,"订单状态不符"),
    SYSTEM_ERROR(20000,"系统异常");
    Integer code;
    String msg;

    OnlineStore_ExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
