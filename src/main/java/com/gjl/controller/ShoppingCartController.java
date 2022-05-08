package com.gjl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gjl.common.R;
import com.gjl.domain.ShoppingCart;
import com.gjl.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        return shoppingCartService.add(shoppingCart);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        return shoppingCartService.sub(shoppingCart);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        return shoppingCartService.listSPC();
    }

    @DeleteMapping("/clean")
    public R<String> delete(){
        shoppingCartService.Del();
        return R.success("清空成功！");
    }

}