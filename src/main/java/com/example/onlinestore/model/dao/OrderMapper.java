package com.example.onlinestore.model.dao;

import com.example.onlinestore.model.pojo.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNo(String OrderNo);

    List<Order> selectForCustomer(Integer userId);

    List<Order> selectForAdmin();
}