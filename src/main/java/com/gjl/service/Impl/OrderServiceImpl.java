package com.gjl.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjl.common.BaseContext;
import com.gjl.common.CustomException;
import com.gjl.common.R;
import com.gjl.domain.*;
import com.gjl.dto.OrdersDto;
import com.gjl.mapper.OrderMapper;
import com.gjl.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 外卖下单，添加订单明细
     * @param orders
     */
    @Override
    @CacheEvict("orderCache")
    public void submit(Orders orders) {
        Long userId= BaseContext.GetCurrentId();

        //查询用户的购物车信息
        LambdaQueryWrapper<ShoppingCart> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList=shoppingCartService.list();

        //查询用户的信息
        User user = userService.getById(userId);
        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook==null){
            throw new CustomException("地址为空，不能下单");
        }

        long orderId = IdWorker.getId();

        AtomicInteger amount=new AtomicInteger(0);

        List<OrderDetail> orderDetails=shoppingCartList.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        this.save(orders);

        orderDetailService.saveBatch(orderDetails);

        shoppingCartService.remove(wrapper);

    }

    /**
     * 分页查询订单
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    @Cacheable(value = "orderCache",key = "'orderPage'")
    public R<Page> getAll(int page, int pageSize, Long number, String beginTime, String endTime) {
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(number!=null,Orders::getNumber,number);
        queryWrapper.between(Strings.isNotEmpty(beginTime)&&Strings.isNotEmpty(endTime),Orders::getCheckoutTime,beginTime,endTime);
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        this.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 用户获取订单
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public R<Page> userPage(int page, int pageSize) {
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        Page<OrdersDto> dtoPage=new Page<>();
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,BaseContext.GetCurrentId());
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        this.page(pageInfo,queryWrapper);

        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> list=records.stream().map((item)->{
            OrdersDto ordersDto=new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            Long ordersId=item.getId();
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderDetail::getOrderId,ordersId);
            ordersDto.setOrderDetails(orderDetailService.list(lambdaQueryWrapper));
            return ordersDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 修改订单状态
     * @param orders
     */
    @Override
    @CacheEvict("orderCache")
    public void updateStatus(Orders orders) {
        this.updateById(orders);
    }

    /**
     * 再来一单
     * @param orders
     * @return
     */
    @Override
    @Cacheable(value = "orderCache",key = "#orders.id",unless = "#orders==null")
    public void again(Orders orders) {
        LambdaQueryWrapper<OrderDetail> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,orders.getId());
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);

        List<ShoppingCart> list=orderDetailList.stream().map((item)->{

            ShoppingCart shoppingCart=new ShoppingCart();
            shoppingCart.setName(item.getName());
            shoppingCart.setImage(item.getImage());
            shoppingCart.setUserId(BaseContext.GetCurrentId());
            if (item.getDishId()!=null) shoppingCart.setDishId(item.getDishId());
            if (item.getSetmealId()!=null) shoppingCart.setSetmealId(item.getSetmealId());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartService.saveBatch(list);
    }


}