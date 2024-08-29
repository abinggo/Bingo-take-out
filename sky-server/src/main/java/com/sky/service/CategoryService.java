package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    List<Category> list(Integer type);

    void update(CategoryDTO categoryDTO);

    PageResult pagequery(CategoryPageQueryDTO categoryPageQueryDTO);

    void startOrstop(Integer status, Long id);

    void save(CategoryDTO categoryDTO);

    void delete(Long id);
}
