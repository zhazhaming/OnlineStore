package com.example.onlinestore.service.impl;


import com.example.onlinestore.common.Constant;
import com.example.onlinestore.exception.OnlineStore_Exception;
import com.example.onlinestore.exception.OnlineStore_ExceptionEnum;
import com.example.onlinestore.model.dao.ProductMapper;
import com.example.onlinestore.model.pojo.Product;
import com.example.onlinestore.model.query.ProductListQuery;
import com.example.onlinestore.model.request.AddProductReq;
import com.example.onlinestore.model.request.ProductListReq;
import com.example.onlinestore.model.request.UpdateCategoryReq;
import com.example.onlinestore.model.vo.CategoryVO;
import com.example.onlinestore.service.CategoryService;
import com.example.onlinestore.service.ProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryService categoryService;

    @Override
    public void add(AddProductReq addProductReq){
        Product product = new Product ();
        BeanUtils.copyProperties (addProductReq,product);
        Product productOld = productMapper.selectByName (addProductReq.getName ( ));
        if (productOld!=null){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.insertSelective (product);
        if (count==0){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public void update(Product updateProductReq){
        Product productOld = productMapper.selectByName (updateProductReq.getName ( ));
        if (productOld != null && ! productOld.getId ().equals (updateProductReq.getId ())){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.updateByPrimaryKeySelective(updateProductReq);
        if (count == 0){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.UPDATE_FAILE);
        }
    }

    @Override
    public void delete(Integer id){
        Product productOld = productMapper.selectByPrimaryKey (id);
        if (productOld == null ){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.UPDATE_FAILE);
        }
        int count = productMapper.deleteByPrimaryKey (id);
        if (count == 0){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.UPDATE_FAILE);
        }
    }
    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus){
        productMapper.batchUpdateSellStatus (ids, sellStatus);
    }

    @Override
    public PageInfo listForadmin(Integer pageNum, Integer pageSize){
        //????????????
        PageHelper.startPage (pageNum,pageSize);
        List<Product> products = productMapper.listForAdmin ( );
        PageInfo pageInfo = new PageInfo ( products);
        return pageInfo;
    }

    @Override
    public Product detail(Integer id){
        Product product = productMapper.selectByPrimaryKey (id);
        return product;
    }

    @Override
    public PageInfo list(ProductListReq productListReq){
        //??????query??????
        ProductListQuery productListQuery = new ProductListQuery ( );
        // ????????????
        if (!StringUtils.isEmpty (productListReq.getKeyword ())){
            String keyword = new StringBuffer ( ).append ("%").append (productListReq.getKeyword ( )).append ("%").toString ();
            productListQuery.setKeyword (keyword);
        }
        //????????????
        if (productListReq.getCategoryId ()!=null){
            List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer ( productListReq.getCategoryId ());
            ArrayList<Integer> categoryIds = new ArrayList<> ( );
            categoryIds.add (productListReq.getCategoryId ());
            getCategoryIds (categoryVOList,categoryIds);
            productListQuery.setCategoryIds (categoryIds);
        }
        //????????????
         String orderBy = productListReq.getOrderBy ();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains (orderBy)){
            PageHelper.startPage (productListReq.getPageNum (),productListReq.getPageSize (),orderBy);
        }else {
            PageHelper.startPage (productListReq.getPageNum (),productListReq.getPageSize ());
        }
        List<Product> productList = productMapper.selectList (productListQuery);
        return new PageInfo ( productList );
    }

    private void getCategoryIds(List<CategoryVO> categoryVOList,ArrayList<Integer> categoryIds){
        for (int i = 0; i < categoryVOList.size ( ); i++) {
            CategoryVO categoryVO =  categoryVOList.get (i);
            if (categoryVO!=null){
                categoryIds.add (categoryVO.getId ());
                getCategoryIds (categoryVO.getChildCategory (),categoryIds);
            }
        }
    }

}
