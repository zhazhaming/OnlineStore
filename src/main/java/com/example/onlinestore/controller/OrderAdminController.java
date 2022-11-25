package com.example.onlinestore.controller;

import com.example.onlinestore.common.ApiRestResponse;
import com.example.onlinestore.service.OrderService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderAdminController {
    @Autowired
    OrderService orderService;

    @GetMapping("/admin/order/list")
    @ApiOperation ("管理员订单列表")
    public ApiRestResponse listForAdmin(@RequestParam Integer pageNum,@RequestParam Integer pageSize){
        PageInfo pageInfo = orderService.listForAdmin (pageNum, pageSize);
        return ApiRestResponse.success (pageInfo);
    }

    @PostMapping("/admin/order/delivered")
    @ApiOperation ("订单发货")
    public ApiRestResponse delivered(String orderNo){
        orderService.delivered (orderNo);
        return ApiRestResponse.success ();
    }

    @PostMapping("order/finish")
    @ApiOperation ("订单发货")
    public ApiRestResponse finish(String orderNo){
        orderService.finish (orderNo);
        return ApiRestResponse.success ();
    }

}
