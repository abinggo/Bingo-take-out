package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 查看购物车方法
     * @return
     */
     List<ShoppingCart> showShoppingCart();

    /**
     * 添加购物车业务方法
     * @param shoppingCartDTO
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    void sub(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     */
    void clean();
}
