package com.example.onlinestore.service;

import com.example.onlinestore.model.vo.CartVO;

import java.util.List;


public interface CartService {


    List<CartVO> list(Integer userId);

    List<CartVO> addCart(Integer userId, Integer proeductId, Integer count);

    List<CartVO> updateCart(Integer userId, Integer proeductId, Integer count);

    List<CartVO> deleteCart(Integer userId, Integer proeductId);

    List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected);

    List<CartVO> selectALLOrNot(Integer userId, Integer selected);
}
