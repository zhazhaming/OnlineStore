package com.example.onlinestore.controller;


import com.example.onlinestore.common.ApiRestResponse;
import com.example.onlinestore.filter.UserFilter;
import com.example.onlinestore.model.vo.CartVO;
import com.example.onlinestore.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/list")
    @ApiOperation ("购物车列表")
    public ApiRestResponse list(){
        List<CartVO> list = cartService.list (UserFilter.currUser.getId ( ));
        return ApiRestResponse.success (list);
    }

    @PostMapping("/add")
    @ApiOperation ("添加到购物车")
    public ApiRestResponse add(@RequestParam Integer productId,@RequestParam Integer count){
        List<CartVO> cartVOS = cartService.addCart (UserFilter.currUser.getId ( ), productId, count);
        return ApiRestResponse.success (cartVOS);
    }

    @PostMapping("/update")
    @ApiOperation ("更新购物车")
    public ApiRestResponse update(@RequestParam Integer productId,@RequestParam Integer count){
        List<CartVO> cartVOS = cartService.updateCart (UserFilter.currUser.getId ( ), productId, count);
        return ApiRestResponse.success (cartVOS);
    }

    @PostMapping("/delete")
    @ApiOperation ("删除购物车")
    public ApiRestResponse deleteCart(@RequestParam Integer productId){
        List<CartVO> cartVOS = cartService.deleteCart (UserFilter.currUser.getId ( ), productId);
        return ApiRestResponse.success (cartVOS);
    }

    @PostMapping("/select")
    @ApiOperation ("选择/不选购物车中的商品")
    public ApiRestResponse select(@RequestParam Integer productId,@RequestParam Integer selected){
        List<CartVO> cartVOS = cartService.selectOrNot (UserFilter.currUser.getId ( ), productId,selected);
        return ApiRestResponse.success (cartVOS);
    }

    @PostMapping("/selectAll")
    @ApiOperation ("全选择/全不选购物车中的商品")
    public ApiRestResponse selectAll(@RequestParam Integer selected){
        List<CartVO> cartVOS = cartService.selectALLOrNot (UserFilter.currUser.getId ( ),selected);
        return ApiRestResponse.success (cartVOS);
    }
}
