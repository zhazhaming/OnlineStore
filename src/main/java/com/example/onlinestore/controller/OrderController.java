package com.example.onlinestore.controller;

import com.example.onlinestore.common.ApiRestResponse;
import com.example.onlinestore.model.request.CreateOrderReq;
import com.example.onlinestore.model.vo.OrderVO;
import com.example.onlinestore.service.OrderService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/create")
    @ApiOperation ("创建订单")
    public ApiRestResponse create(@RequestBody CreateOrderReq createOrderReq){
        String OrderNo = orderService.create (createOrderReq);
        return ApiRestResponse.success (OrderNo);
    }

    @GetMapping("/detail")
    @ApiOperation ("前台订单详情")
    public ApiRestResponse detail(@RequestParam String orderNo){
        OrderVO detail = orderService.detail (orderNo);
        return ApiRestResponse.success (detail);
    }

    @GetMapping("/list")
    @ApiOperation ("前台订单列表")
    public ApiRestResponse detail(@RequestParam Integer pageNum,@RequestParam Integer pageSize){
        PageInfo pageInfo = orderService.listForCustomer (pageNum, pageSize);
        return ApiRestResponse.success (pageInfo);
    }

    @PostMapping("/cancel")
    @ApiOperation ("前台取消订单")
    public ApiRestResponse cancel(@RequestParam String orderNo){
        orderService.cancel (orderNo);
        return ApiRestResponse.success ();
    }

    @GetMapping("/qrcode")
    @ApiOperation ("生成支付二维码")
    public ApiRestResponse qrcode(@RequestParam String orderNo){
        String pngAdderss = orderService.qrcode (orderNo);
        orderService.pay (orderNo); //由于前端原因，这里下单即付款
        return ApiRestResponse.success (pngAdderss);
    }

    @GetMapping("/pay")
    @ApiOperation ("支付接口")
    public ApiRestResponse pay(@RequestParam String orderNo){
        orderService.pay(orderNo);
        return ApiRestResponse.success ();
    }
}
