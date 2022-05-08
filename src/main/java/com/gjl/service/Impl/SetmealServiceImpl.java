package com.gjl.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjl.common.CustomException;
import com.gjl.common.R;
import com.gjl.domain.Category;
import com.gjl.domain.Setmeal;
import com.gjl.domain.SetmealDish;
import com.gjl.dto.SetmealDto;
import com.gjl.mapper.SetmealMapper;
import com.gjl.service.CategoryService;
import com.gjl.service.SetmealDishService;
import com.gjl.service.SetmealService;
import lombok.val;
import lombok.var;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes=setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 套餐分页数据展示
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        Page<SetmealDto> pageDto=new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        this.page(pageInfo);

        BeanUtils.copyProperties(pageInfo,pageDto,"records");

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list=records.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        pageDto.setRecords(list);

        return R.success(pageDto);
    }

    /**
     * 获取点击修改时当前的套餐信息进行回显
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithSetmealDish(Long id) {
        Setmeal setmeal=this.getById(id);
        SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        queryWrapper.orderByAsc(SetmealDish::getSort).orderByDesc(SetmealDish::getUpdateTime);

        setmealDto.setSetmealDishes(setmealDishService.list(queryWrapper));
        return setmealDto;
    }

    /**
     * 修改套餐
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithSetmealDish(SetmealDto setmealDto) {
        //先修改套餐表里的数据
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes=setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 起售停售套餐
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(List<Long> ids, Integer status) {
        List<Setmeal> list=new ArrayList<>();
        for (Long id : ids) {
            Setmeal setmeal=new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(status);
            list.add(setmeal);
        }
        this.updateBatchById(list);
    }

    @Override
    @Transactional
    public void deleteSetmeal(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        if(this.count(queryWrapper)>0){
            throw new CustomException("套餐正在售卖中，不能删除，请先停售套餐！");
        }

        for (Long id : ids) {
            LambdaQueryWrapper<SetmealDish> queryWrapper1=new LambdaQueryWrapper<>();
            queryWrapper1.eq(SetmealDish::getSetmealId,id);
            setmealDishService.remove(queryWrapper1);
        }
        this.removeByIds(ids);
    }

    /**
     * 获取套餐分类下面的套餐信息
     * @param setmeal
     * @return
     */
    @Override
    public R<List<SetmealDto>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus,1);
        queryWrapper.orderByDesc(Setmeal::getCreateTime);

        List<Setmeal> list = this.list(queryWrapper);

        List<SetmealDto> setmealDtoList=list.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long id = setmealDto.getId();

            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
            setmealDto.setSetmealDishes(setmealDishService.list(lambdaQueryWrapper));
            return setmealDto;
        }).collect(Collectors.toList());
        return R.success(setmealDtoList);
    }
}
