package com.sky.controller.admin;

import com.github.pagehelper.Constant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "客户端订单管理接口")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
 public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
     log.info("订单搜索",ordersPageQueryDTO);
     PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
     return Result.success(pageResult);
 }
 @GetMapping("/statistics")
 @ApiOperation("各个状态订单数量统计")
 public Result<OrderStatisticsVO> staticOrder(){
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
 }
 @PutMapping("/confirm")
 @ApiOperation("接单")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirmOrder(ordersConfirmDTO);
        return Result.success();
 }
 @PutMapping("/complete/{id}")
 @ApiOperation("完成订单")
    public Result completeOrder(@PathVariable Long id){
        orderService.completeOrder(id);
        return Result.success();
 }
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result deliveryOrder(@PathVariable Long id){
        orderService.deliveryOrder(id);
        return Result.success();
    }
    @PutMapping("/rejection")
    @ApiOperation("取消订单")
    public Result CancelOrders(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.cancelOrders(ordersRejectionDTO);
        return Result.success();
    }
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> fullOrder(@PathVariable Long id){
        OrderVO orderVO = orderService.getByOrderId(id);
        return Result.success(orderVO);
    }


}
