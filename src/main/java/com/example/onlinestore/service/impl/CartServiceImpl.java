package com.example.onlinestore.service.impl;

import com.example.onlinestore.common.Constant;
import com.example.onlinestore.exception.OnlineStore_Exception;
import com.example.onlinestore.exception.OnlineStore_ExceptionEnum;
import com.example.onlinestore.model.dao.CartMapper;
import com.example.onlinestore.model.dao.ProductMapper;
import com.example.onlinestore.model.pojo.Cart;
import com.example.onlinestore.model.pojo.Product;
import com.example.onlinestore.model.vo.CartVO;
import com.example.onlinestore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CartMapper cartMapper;

    @Override
    public List<CartVO> list(Integer userId){
        List<CartVO> cartVOS = cartMapper.selectList (userId);
        for (CartVO cartVO : cartVOS) {
            cartVO.setTotalprice (cartVO.getPrice ()*cartVO.getQuantity ());
        }
        return cartVOS;
    }

    @Override
    public List<CartVO> addCart(Integer userId,Integer productId, Integer count){
        vaildProduct(productId,count);
        Cart cart = cartMapper.selectByProductIdAndUserId (productId, userId);
        if (cart==null){
            // 用户此前没有添加过这个商品
            Cart newcart = new Cart ( );
            newcart.setProductId (productId);
            newcart.setUserId (userId);
            newcart.setQuantity (count);
            newcart.setSelected (Constant.CartStatus.CHECKED);
            cartMapper.insertSelective (newcart);
        }else {
            //购物车已经有该商品,可以添加
            Cart cartAdd = new Cart ( );
            cartAdd.setId (cart.getId ());
            cartAdd.setProductId (cart.getProductId ());
            cartAdd.setUserId (cart.getUserId ());
            cartAdd.setQuantity (cart.getQuantity ()+count);
            cartAdd.setSelected (Constant.CartStatus.CHECKED);
            cartMapper.updateByPrimaryKeySelective (cartAdd);
        }
        return this.list (userId);
    }

    // 判断商品状态和库存是否满足要求
    private void vaildProduct(Integer productId, Integer count) {
        //判断商品是否存在、状态是否上架
        Product product = productMapper.selectByPrimaryKey (productId);
        if (product==null || product.getStatus ().equals (Constant.SaleStatus.NOT_SALE)){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NOT_SALE);
        }
        //判断商品库存
        if (count>product.getStock ()){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NOT_ENOUGH);
        }
    }
    @Override
    public List<CartVO> updateCart(Integer userId, Integer productId, Integer count){
        vaildProduct(productId,count);
        Cart cart = cartMapper.selectByProductIdAndUserId (productId, userId);
        if (cart==null){
            // 用户此前没有添加过这个商品
            throw new OnlineStore_Exception(OnlineStore_ExceptionEnum.UPDATE_FAILE);
        }else {
            //购物车已经有该商品,可以更新
            Cart cartAdd = new Cart ( );
            cartAdd.setId (cart.getId ());
            cartAdd.setProductId (cart.getProductId ());
            cartAdd.setUserId (cart.getUserId ());
            cartAdd.setQuantity (count);
            cartAdd.setSelected (Constant.CartStatus.CHECKED);
            cartMapper.updateByPrimaryKeySelective (cartAdd);
        }
        return this.list (userId);
    }

    @Override
    public List<CartVO> deleteCart(Integer userId, Integer productId){
        Cart cart = cartMapper.selectByProductIdAndUserId (productId, userId);
        if (cart==null){
            // 用户此前没有添加过这个商品
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.DELETE_FAILE);
        }else {
            //购物车已经有该商品,可以删除
            cartMapper.deleteByPrimaryKey (cart.getId ());
        }
        return this.list (userId);
    }

    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected){
        Cart cart = cartMapper.selectByProductIdAndUserId (productId, userId);
        if (cart == null){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.UPDATE_FAILE);
        }else{
            cartMapper.selectOrNot (productId,userId,selected);
        }
        return this.list (userId);
    }

    @Override
    public List<CartVO> selectALLOrNot(Integer userId, Integer selected){
        cartMapper.selectOrNot (null,userId,selected);
        return this.list (userId);
    }
}
