package com.example.onlinestore.service;

import com.example.onlinestore.model.request.CreateOrderReq;
import com.example.onlinestore.model.vo.OrderVO;
import com.github.pagehelper.PageInfo;

public interface OrderService {
    String create(CreateOrderReq createOrderReq);

    OrderVO detail(String OrderNo);

    PageInfo listForCustomer(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);

    String qrcode(String orderNo);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    void pay(String orderNo);

    void delivered(String orderNo);

    void finish(String orderNo);
}
