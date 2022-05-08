package com.gjl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gjl.common.R;
import com.gjl.domain.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {

    public R<ShoppingCart> add(ShoppingCart shoppingCart);

    public R<ShoppingCart> sub(ShoppingCart shoppingCart);

    public R<List<ShoppingCart>> listSPC();

    public void Del();
}
