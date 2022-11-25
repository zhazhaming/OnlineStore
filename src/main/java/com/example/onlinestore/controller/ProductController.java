package com.example.onlinestore.controller;

import com.example.onlinestore.common.ApiRestResponse;
import com.example.onlinestore.model.pojo.Product;
import com.example.onlinestore.model.request.ProductListReq;
import com.example.onlinestore.service.ProductService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    @Autowired
    ProductService productService;

    @ApiOperation ("商品详情")
    @GetMapping("/product/detail")
    public ApiRestResponse detail(Integer id){
        Product detail = productService.detail (id);
        return ApiRestResponse.success (detail);
    }

    @ApiOperation ("商品列表")
    @GetMapping("/product/list")
    public ApiRestResponse list(ProductListReq productListReq){
        PageInfo list = productService.list (productListReq);
        return ApiRestResponse.success (list);
    }
}
