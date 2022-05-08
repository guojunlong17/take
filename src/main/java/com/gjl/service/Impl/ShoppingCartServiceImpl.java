package com.gjl.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjl.common.BaseContext;
import com.gjl.common.R;
import com.gjl.domain.ShoppingCart;
import com.gjl.mapper.ShoppingCartMapper;
import com.gjl.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    /**
     * 增加到购物车
     * @param shoppingCart
     * @return
     */
    @Override
    public R<ShoppingCart> add(ShoppingCart shoppingCart) {

        shoppingCart.setUserId(BaseContext.GetCurrentId());

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());

        if (shoppingCart.getDishId()!=null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart one = this.getOne(queryWrapper);

        if (one!=null){
            one.setNumber(one.getNumber()+1);
            this.updateById(one);
        }else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            one=shoppingCart;
        }
        return R.success(one);
    }

    /**
     * 点减号菜品减一，低于一时删除
     * @param shoppingCart
     * @return
     */
    @Override
    public R<ShoppingCart> sub(ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.GetCurrentId());

        if (shoppingCart.getDishId()!=null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart shoppingCartOne=this.getOne(queryWrapper);

        Integer numbers=shoppingCartOne.getNumber();
        if (numbers<=1){
            this.removeById(shoppingCartOne.getId());
            ShoppingCart shoppingCart1=new ShoppingCart();
            shoppingCart1.setNumber(0);
            return R.success(shoppingCart1);
        }else {
            shoppingCartOne.setNumber(numbers-1);
            this.updateById(shoppingCartOne);
        }

        return R.success(shoppingCartOne);
    }

    /**
     * 获取购物车集合
     * @return
     */
    @Override
    public R<List<ShoppingCart>> listSPC() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.GetCurrentId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        return R.success(this.list(queryWrapper));
    }

    /**
     * 全部删除购物车
     */
    @Override
    public void Del() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.GetCurrentId());
        this.remove(queryWrapper);
    }
}
