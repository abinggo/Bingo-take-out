package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
    public Result<List<DishVO>> getByTypeId(Long categoryId){
        //升级版
        //构造key
        String key = "dish_" + categoryId;
        //查询redis中是都存在菜品数据

        List<DishVO> out = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //如果存在，直接返回，无需查询数据库
        if(out != null && out.size()>0){
            return Result.success(out);
        }
        //如果不存在，则查询数据库，将查询到的数据放入redis中
        else{
        log.info("查看分类id",categoryId);
        List<Dish> list =  dishService.getByTypeId(categoryId);
        out = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            DishVO dishVO = new DishVO();
            dishVO = dishService.getByIdWithFlavor(list.get(i).getId());
            out.add(dishVO);
        }
        //放入数据库
        redisTemplate.opsForValue().set(key,out);
        return Result.success(out);
    }
    }
}
