package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增套餐相关数据
     * @param setmealDTO
     */
    @Transactional
    public void saveWithDishes(@RequestBody  SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //向套餐中插入一条数据
        setmealMapper.insert(setmeal);

        Long setmealId = setmeal.getId();
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        if (dishes != null && dishes.size() > 0) {
            dishes.forEach(dish -> {
                dish.setSetmealId(setmealId);
            });
            //将相关菜品表中批量插入n条数据
            setmealDishMapper.insertBatch(dishes);

        }
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 更新套餐
     * @param setmealDTO
     */
    public void updateWithDishes(SetmealDTO setmealDTO) {
        //对于绑定的菜品 先删除 再插入
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        //先更新基本信息 不包括关联菜品
        setmealMapper.update(setmeal);
        //删除原先菜品
        setmealDishMapper.deleteById(setmealDTO.getId());
        //插入新的绑定菜品
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        if(dishes!=null&&dishes.size()>0){
            dishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealDTO.getId());
            });
            setmealDishMapper.insertBatch(dishes);
        }
    }

    /**
     * 套餐批量删除
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //好像没有很复杂的需求 可以直接删除
        setmealMapper.deleteByIds(ids);
        //删除套餐关联的相关菜品数据
        setmealDishMapper.deleteByIds(ids);
    }

    /**
     * 根据ID查询套餐
     * @param id
     * @return
     */
    public SetmealVO getByIdwithDishes(Long id) {
        //记住封装对象
        Setmeal setmeal = setmealMapper.getById(id);
        //根据ID查询相关菜品数据
        List<SetmealDish> setmealDishes = setmealDishMapper.getById(id);
        //将查询到的数据封装到VO中
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 起售/停售
     * @param status
     * @param id
     */
    public void startOrstop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder().status(status).id(id).build();
        setmealMapper.update(setmeal);
    }
}
