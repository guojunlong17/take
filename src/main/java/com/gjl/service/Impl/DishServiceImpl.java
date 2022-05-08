package com.gjl.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjl.common.CustomException;
import com.gjl.common.R;
import com.gjl.domain.Category;
import com.gjl.domain.Dish;
import com.gjl.domain.DishFlavor;
import com.gjl.dto.DishDto;
import com.gjl.mapper.DishMapper;
import com.gjl.service.CategoryService;
import com.gjl.service.DishFlavorService;
import com.gjl.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        Long dishId=dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors=flavors.stream().map((item) -> {
           item.setDishId(dishId);
           return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public R<Page> getAll(int page, int pageSize, String name) {
        //创建菜品分页page
        Page<Dish> pageInfo=new Page<>(page,pageSize);
        //创建菜品分页pageDto
        Page<DishDto> pageDto=new Page<>();

        LambdaQueryWrapper<Dish> qw=new LambdaQueryWrapper<>();

        qw.like(name!=null,Dish::getName,name);

        qw.orderByDesc(Dish::getUpdateTime);

        this.page(pageInfo,qw);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        //获取菜品信息的list
        List<Dish> records=pageInfo.getRecords();
        //对菜品信息的list进行遍历
        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long CategoryId=item.getCategoryId();//分类id
            Category category = categoryService.getById(CategoryId);
            if (category!=null){
                String categoryName=category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        pageDto.setRecords(list);
        return R.success(pageDto);
    }

    /**
     * 修改时获取当前的菜品信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish=this.getById(id);

        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> qw=new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list=dishFlavorService.list(qw);
        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     * 修改菜品
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> qw=new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(qw);

        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors=flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteDish(List<Long> ids) {

        LambdaQueryWrapper<Dish> DishQueryWrapper=new LambdaQueryWrapper<>();
        DishQueryWrapper.in(Dish::getId,ids);
        DishQueryWrapper.eq(Dish::getStatus,1);

        if (this.count(DishQueryWrapper)>0){
            throw new CustomException("菜品正在售卖中，不能删除，请先停售菜品！");
        }

        for (Long id : ids) {
            LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,id);
            dishFlavorService.remove(queryWrapper);
        }

        this.removeByIds(ids);
    }

    /**
     * 停售起售菜品
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(List<Long> ids, Integer status) {
        List<Dish> list=new ArrayList<Dish>();
        for (Long id : ids) {
            Dish dish=new Dish();
            dish.setId(id);
            dish.setStatus(status);
            list.add(dish);
        }
        this.updateBatchById(list);
    }

    /**
     * 获取菜品分类下售卖中的菜品信息
     * @param dish
     * @return
     */
    @Override
    public R<List<DishDto>> list(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = this.list(queryWrapper);

        List<DishDto> dtoList=list.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> list1 = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dtoList);
    }

}
