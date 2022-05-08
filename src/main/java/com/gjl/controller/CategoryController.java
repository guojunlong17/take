package com.gjl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjl.common.R;
import com.gjl.domain.Category;
import com.gjl.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService service;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        service.save(category);
        return R.success("添加成功！");
    }

    @GetMapping("/page")
    public R<Page> getAll(int page,int pageSize){
        return service.GetAll(page,pageSize);
    }

    @DeleteMapping
    public R<String> delete(Long ids){
        return service.remove(ids);
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        service.updateById(category);
        return R.success("");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        return service.GetTypeAll(category);
    }

}
