package com.gjl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjl.common.R;
import com.gjl.domain.Orders;
import com.gjl.domain.ShoppingCart;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);

    public R<Page> getAll(int page, int pageSize, Long number, String beginTime, String endTime);

    public R<Page> userPage(int page, int pageSize);

    public void updateStatus(Orders orders);

    public void again(Orders orders);
}
