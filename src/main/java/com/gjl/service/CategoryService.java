package com.gjl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjl.common.R;
import com.gjl.domain.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    //分页查询所有菜品分类数据
    public R<Page> GetAll(int page,int pageSize);
    //删除菜品分类
    public R<String> remove(Long id);
    //查询菜品分类
    public R<List<Category>> GetTypeAll(Category category);
}
