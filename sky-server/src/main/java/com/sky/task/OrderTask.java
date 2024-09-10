package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder(){
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        LocalDateTime ordertime = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.getBystatusAndOrderTime(Orders.PENDING_PAYMENT, ordertime);
        if(ordersList != null && ordersList.size()>0){
            for (int i = 0; i < ordersList.size(); i++) {
                Orders orders = ordersList.get(i);
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }

    }

    /**
     * 处理一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")

    public void processDeliveryOrder(){
        log.info("定时处理派送中订单：{}", LocalDateTime.now());
        LocalDateTime ordertime = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getBystatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, ordertime);
        if(ordersList != null && ordersList.size()>0){
            for (int i = 0; i < ordersList.size(); i++) {
                Orders orders = ordersList.get(i);
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
