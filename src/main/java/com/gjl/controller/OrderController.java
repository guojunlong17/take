package com.gjl.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjl.common.R;
import com.gjl.domain.Orders;
import com.gjl.domain.ShoppingCart;
import com.gjl.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,@RequestParam(required = false)
            Long number,@RequestParam(required = false)
            String beginTime,@RequestParam(required = false) String endTime){

        return orderService.getAll(page, pageSize, number, beginTime, endTime);
    }

    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize){
        return orderService.userPage(page,pageSize);
    }

    @PutMapping
    public R<String> update(@RequestBody Orders orders){
        orderService.updateStatus(orders);
        return R.success("派送成功");
    }

    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        orderService.again(orders);
        return R.success("");
    }

}