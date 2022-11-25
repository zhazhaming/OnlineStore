package com.example.onlinestore.service;

import com.example.onlinestore.model.pojo.Category;
import com.example.onlinestore.model.request.AddCategoryReq;
import com.example.onlinestore.model.vo.CategoryVO;
import com.github.pagehelper.PageInfo;
import springfox.documentation.annotations.Cacheable;

import java.util.List;

public interface CategoryService {

    void addCategory(AddCategoryReq addCategoryReq);

    void updateCategory(Category updateCategoryReq);

    void deleteCategory(Integer id);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    @Cacheable(value = "listCategoryForCustomer")
    List<CategoryVO> listCategoryForCustomer(Integer parentId);
}
