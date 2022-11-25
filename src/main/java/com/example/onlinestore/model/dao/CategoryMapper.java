package com.example.onlinestore.model.dao;

import com.example.onlinestore.model.pojo.Category;
import com.example.onlinestore.model.vo.CategoryVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    Category selectByName(String Name);

    List<Category> selectList();

    List<Category> selectByparenetId(Integer parentId);
}