package com.example.onlinestore.controller;

import com.example.onlinestore.common.ApiRestResponse;
import com.example.onlinestore.common.Constant;
import com.example.onlinestore.exception.OnlineStore_ExceptionEnum;
import com.example.onlinestore.model.pojo.Category;
import com.example.onlinestore.model.pojo.User;
import com.example.onlinestore.model.request.AddCategoryReq;
import com.example.onlinestore.model.request.UpdateCategoryReq;
import com.example.onlinestore.model.vo.CategoryVO;
import com.example.onlinestore.service.CategoryService;
import com.example.onlinestore.service.UserService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * 目录Controller
 */

@Controller
public class CategoryController {

    @Autowired
    UserService userService;
    @Autowired
    CategoryService categoryService;

    @ApiOperation ("后台添加目录")
    @PostMapping("/admin/category/add")
    @ResponseBody
    public ApiRestResponse addCategory(HttpSession session, @Valid @RequestBody AddCategoryReq addCategoryReq){
        //判断当前是否登录了用户
        User currUser = (User) session.getAttribute (Constant.ONLINESTORE_USER);
        if (currUser == null){
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.NEED_LOGIN);
        }
        //校验是不是管理员
        boolean adminRole = userService.checkAdminRole (currUser);
        if (adminRole){
            categoryService.addCategory (addCategoryReq);
            return ApiRestResponse.success ();
        }else {
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.NEED_ADMIN);
        }
    }

    @ApiOperation ("后台更新目录")
    @PostMapping("/admin/category/update")
    @ResponseBody
    public ApiRestResponse updateCategory(HttpSession session, @Valid @RequestBody UpdateCategoryReq updateCategoryReq){
        //判断当前是否登录了用户
        User currUser = (User) session.getAttribute (Constant.ONLINESTORE_USER);
        if (currUser == null){
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.NEED_LOGIN);
        }
        //校验是不是管理员
        boolean adminRole = userService.checkAdminRole (currUser);
        if (adminRole){
            Category category = new Category ();
            BeanUtils.copyProperties (updateCategoryReq,category );
            categoryService.updateCategory (category);
            return ApiRestResponse.success ();
        }else {
            return ApiRestResponse.error (OnlineStore_ExceptionEnum.NEED_ADMIN);
        }
    }

    @ApiOperation ("后台删除目录")
    @PostMapping("/admin/category/delete")
    @ResponseBody
    public ApiRestResponse deleteCategory(@RequestParam Integer id){
        categoryService.deleteCategory (id);
        return ApiRestResponse.success ();
    }


    @ApiOperation ("后台目录列表")
    @GetMapping("/admin/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum,@RequestParam Integer pageSize){
        PageInfo pageInfo = categoryService.listForAdmin (pageNum, pageSize);
        return ApiRestResponse.success (pageInfo);
    }

    @ApiOperation ("前台目录列表")
    @GetMapping("/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForCustomer(){
        List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer (0);
        return ApiRestResponse.success (categoryVOList);
    }

}
