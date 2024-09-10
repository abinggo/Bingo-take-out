package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private UserMapper userMapper;
    /**
     * 用户下单 hejiahuanl
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //零 处理异常数据
        //先处理用户地址信息
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //然后处理用户是否的购物车数据是否为空
        Long UserId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(UserId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list==null && list.size()==0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //一 向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        //设置时间戳
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(UserId);
        orderMapper.insert(orders);
        //二 向订单明细表插入n条数据

        //批量插入效率更高
        List<OrderDetail> detaillist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ShoppingCart cart = list.get(i);
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            detaillist.add(orderDetail);
        }
        orderDetailMapper.insert(detaillist);
        //三 清空购物车数据
        shoppingCartMapper.deleteByUserId(UserId);
        //四 返回结果
        OrderSubmitVO OVo= OrderSubmitVO.builder().id(orders.getId()).orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber()).orderAmount(orders.getAmount()).build();
        return OVo;
    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "Bingo外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 历史订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        //ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        //先分页查询每个orders，再进行封装
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> listout = new ArrayList<>();
        if(page!=null&&page.size()>0)
        {
        for (int i = 0; i < page.size() ; i++) {
            Orders orders = page.get(i);
            List<OrderDetail> orderDetail =  orderDetailMapper.getByOrderId(orders.getId());
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders,orderVO);
            orderVO.setOrderDetailList(orderDetail);
            listout.add(orderVO);
        }
        }
        return new PageResult(page.getTotal(),listout);
    }

    /**
     * 根据id获取订单详细信息
     * @param id
     * @return
     */
    public OrderVO getByOrderId(Long id) {
        Orders orders = orderMapper.getById(id);
        List<OrderDetail> orderDetail =  orderDetailMapper.getByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetail);
        return orderVO;
    }

    /**
     * 再来一单
     * @param id
     */
    @Transactional
    public void againById(Long id) {
        //向订单表中添加订单 错了再来一单是往购物车里添加数据

        List<OrderDetail> detail = orderDetailMapper.getByOrderId(id);
        for (int i = 0; i < detail.size(); i++) {
            OrderDetail orderDetail = detail.get(i);
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCartMapper.insert(shoppingCart);
            }
        }

    /**
     * 统计各个状态的数量
     * @return
     */
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        int tobeconfirmed = orderMapper.selectnum(2);
        int confirmed = orderMapper.selectnum(3);
        int deliveryNum = orderMapper.selectnum(4);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setToBeConfirmed(tobeconfirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryNum);
        return orderStatisticsVO;
        //select * from order where status = {?}

    }

    /**
     * 接单的实现
     * @param ordersConfirmDTO
     */
    public void confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        ordersConfirmDTO.setStatus(Orders.CONFIRMED);
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersConfirmDTO,orders);
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     * @param id
     */
    public void completeOrder(Long id) {
        Orders orders = Orders.builder().id(id).status(Orders.COMPLETED).build();
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     */
    public void deliveryOrder(Long id) {
        Orders orders = Orders.builder().id(id).status(Orders.DELIVERY_IN_PROGRESS).build();
        orderMapper.update(orders);
    }

    /**
     * 取消订单
     * @param ordersRejectionDTO
     */
    public void cancelOrders(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = Orders.builder().status(Orders.CANCELLED).id(ordersRejectionDTO.getId()).build();
        orderMapper.update(orders);
    }

}

