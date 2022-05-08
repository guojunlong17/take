package com.gjl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjl.common.R;
import com.gjl.domain.Setmeal;
import com.gjl.dto.SetmealDto;
import com.gjl.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    public R<String> list(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("新增成功！");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        return setmealService.page(page,pageSize,name);
    }

    @GetMapping("{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        return R.success(setmealService.getByIdWithSetmealDish(id));
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithSetmealDish(setmealDto);
        return R.success("修改套餐成功！");
    }

    @PostMapping("status/{status}")
    public R<String> update(@RequestParam List<Long> ids,@PathVariable Integer status){
        setmealService.updateStatus(ids,status);
        return R.success("");
    }

    @DeleteMapping
    public R<String> Del(@RequestParam List<Long> ids){
        setmealService.deleteSetmeal(ids);
        return R.success("删除成功！");
    }

    @GetMapping("/list")
    public R<List<SetmealDto>> list(Setmeal setmeal){
        return setmealService.list(setmeal);
    }
}
