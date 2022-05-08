package com.gjl.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjl.common.CustomException;
import com.gjl.common.R;
import com.gjl.domain.Category;
import com.gjl.domain.Dish;
import com.gjl.domain.Setmeal;
import com.gjl.mapper.CategoryMapper;
import com.gjl.service.CategoryService;
import com.gjl.service.DishService;
import com.gjl.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public R<Page> GetAll(int page,int pageSize) {
        Page<Category> pageInfo=new Page<Category>(page,pageSize);
        LambdaQueryWrapper<Category> qw=new LambdaQueryWrapper<>();
        qw.orderByDesc(Category::getSort);
        super.page(pageInfo,qw);
        return R.success(pageInfo);
    }

    @Override
    public R<String> remove(Long id) {
        LambdaQueryWrapper<Dish> dqw=new LambdaQueryWrapper<>();
        dqw.eq(Dish::getCategoryId,id);
        int count1=dishService.count(dqw);
        if (count1>0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> sqw=new LambdaQueryWrapper<>();
        sqw.eq(Setmeal::getCategoryId,id);
        int count2=setmealService.count(sqw);
        if (count2>0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        super.removeById(id);
        return R.success("删除成功！");
    }

    @Override
    public R<List<Category>> GetTypeAll(Category category) {

        LambdaQueryWrapper<Category> qw=new LambdaQueryWrapper<>();
        qw.eq(category.getType()!=null,Category::getType,category.getType());
        qw.orderByDesc(Category::getSort).orderByDesc(Category::getUpdateTime);
        return R.success(super.list(qw));
    }
}
