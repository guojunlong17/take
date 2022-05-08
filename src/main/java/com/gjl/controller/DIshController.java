package com.gjl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjl.common.R;
import com.gjl.domain.Dish;
import com.gjl.dto.DishDto;
import com.gjl.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DIshController {

    @Autowired
    private DishService dishService;

    @PostMapping()
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功！");
    }

    @GetMapping("/page")
    public R<Page> getAll(int page,int pageSize,String name){
        return dishService.getAll(page,pageSize,name);
    }

    @GetMapping("{id}")
    public R<DishDto> getById(@PathVariable Long id){
        return R.success(dishService.getByIdWithFlavor(id));
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功！");
    }

    @DeleteMapping
    public R<String> Del(@RequestParam List<Long> ids){
        dishService.deleteDish(ids);
        return R.success("删除成功！");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus1(@RequestParam List<Long> ids,@PathVariable Integer status){
        dishService.updateStatus(ids,status);
        return R.success("");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        return dishService.list(dish);
    }
}

