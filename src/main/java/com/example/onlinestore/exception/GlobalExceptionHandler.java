package com.example.onlinestore.exception;


import com.example.onlinestore.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.Binding;
import java.util.ArrayList;
import java.util.List;


/**
 * 统一全局异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger (GlobalExceptionHandler.class);

    //系统异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e){
        log.error ("Default Exception:",e);
        return ApiRestResponse.error (OnlineStore_ExceptionEnum.SYSTEM_ERROR);
    }

    //业务异常
    @ExceptionHandler(OnlineStore_Exception.class)
    @ResponseBody
    public Object handleOnlineStoreException(OnlineStore_Exception e){
        log.error ("OnlineStore_Exception:",e);
        return ApiRestResponse.error (e.getCode (),e.getMessage ());
    }

    //处理传入参数错误抛出的异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiRestResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error ("MethodArgumentNotValidException:",e);
        return handleBindingResult (e.getBindingResult ());
    }
    //将异常处理为对外暴露的提示
    private ApiRestResponse handleBindingResult(BindingResult result){
        //list用于放回给前端
        List<String> list = new ArrayList<> (  );
        //获取错误信息传入list
        if (result.hasErrors ()){
            List<ObjectError> allErrors = result.getAllErrors ( );
            for (ObjectError objectError: allErrors) {
                String ErrorMessage = objectError.getDefaultMessage ( );
                list.add (ErrorMessage);
            }
        }
        if (list.size ()==0){
         return ApiRestResponse.error (OnlineStore_ExceptionEnum.REQUEST_PARAM_ERROR);
        }
        return ApiRestResponse.error (OnlineStore_ExceptionEnum.REQUEST_PARAM_ERROR.getCode (),list.toString ());
    }
}
