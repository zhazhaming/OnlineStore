package com.example.onlinestore.service;

import com.example.onlinestore.model.pojo.Product;
import com.example.onlinestore.model.request.AddProductReq;
import com.example.onlinestore.model.request.ProductListReq;
import com.example.onlinestore.model.request.UpdateCategoryReq;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

public interface ProductService {
    void add(AddProductReq addProductReq);

    void update(Product updateCategoryReq);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer status);


    PageInfo listForadmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo list (ProductListReq productListReq);
}
