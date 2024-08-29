package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id来查询对应的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 根据套餐id来批量加入相关菜品
     * @param dishes
     */
    void insertBatch(List<SetmealDish> dishes);

    /**
     * 批量删除对应id的相关菜品信息
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据id删除菜品
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteById(Long id);

    /**
     * 根据Id查询套餐对应菜品
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getById(Long id);
}
