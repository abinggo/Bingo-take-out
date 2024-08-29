package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    void saveWithDishes(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void updateWithDishes(SetmealDTO setmealDTO);

    void deleteBatch(List<Long> ids);

    SetmealVO getByIdwithDishes(Long id);

    void startOrstop(Integer status, Long id);
}
