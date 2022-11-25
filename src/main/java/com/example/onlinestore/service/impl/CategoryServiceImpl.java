package com.example.onlinestore.service.impl;

import com.example.onlinestore.exception.OnlineStore_Exception;
import com.example.onlinestore.exception.OnlineStore_ExceptionEnum;
import com.example.onlinestore.model.dao.CategoryMapper;
import com.example.onlinestore.model.pojo.Category;
import com.example.onlinestore.model.request.AddCategoryReq;
import com.example.onlinestore.model.vo.CategoryVO;
import com.example.onlinestore.service.CategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import springfox.documentation.annotations.Cacheable;


import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public void addCategory(AddCategoryReq addCategoryReq){
        Category category = new Category ();
        BeanUtils.copyProperties (addCategoryReq,category);
        Category categoryOld = categoryMapper.selectByName (addCategoryReq.getName ( ));
        if (categoryOld !=null){
            throw new OnlineStore_Exception(OnlineStore_ExceptionEnum.NAME_EXISTED);
        }
        int count = categoryMapper.insertSelective (category);
        if (count == 0){
            throw  new OnlineStore_Exception (OnlineStore_ExceptionEnum.CREATE_FAILE);
        }
    }

    @Override
    public void updateCategory(Category updateCategoryReq){
        if (updateCategoryReq.getName ()!=null){
            Category categoryOld = categoryMapper.selectByName (updateCategoryReq.getName ( ));
            if (categoryOld !=null && categoryOld.getId ()!=updateCategoryReq.getId ()){
                throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NAME_EXISTED);
            }
        }
        int count = categoryMapper.updateByPrimaryKeySelective (updateCategoryReq);
        if (count == 0){
            throw new OnlineStore_Exception(OnlineStore_ExceptionEnum.UPDATE_FAILE);
        }
    }

    @Override
    public void deleteCategory(Integer id){
        Category categoryOld = categoryMapper.selectByPrimaryKey (id);
        if (categoryOld == null){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.DELETE_FAILE);
        }
        int count = categoryMapper.deleteByPrimaryKey (id);
        if (count==0){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.DELETE_FAILE);
        }
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum,Integer pageSize){
        //开启分页
        PageHelper.startPage (pageNum,pageSize,"type,order_num");
        List<Category> categoriesList = categoryMapper.selectList ( );
        PageInfo<Category> categoryPageInfo = new PageInfo<> (categoriesList);
        return categoryPageInfo;
    }

    @Override
    @Cacheable(value = "listCategoryForCustomer")
    public List<CategoryVO> listCategoryForCustomer(Integer parentId){
        List<CategoryVO> categoryVOList = new ArrayList<> (  );
        recursivelyFindCategories (categoryVOList,parentId);
        return categoryVOList;
    }

    private void recursivelyFindCategories(List<CategoryVO> categoryVOList,Integer parentId){
        List<Category> categoryList = categoryMapper.selectByparenetId (parentId);
        if (!CollectionUtils.isEmpty (categoryList)){
            for (int i = 0; i < categoryList.size (); i++) {
                Category category = categoryList.get (i);
                CategoryVO categoryVO = new CategoryVO ();
                BeanUtils.copyProperties (category,categoryVO);
                categoryVOList.add (categoryVO);
                recursivelyFindCategories (categoryVO.getChildCategory (),categoryVO.getId ());
            }
        }
    }
}
