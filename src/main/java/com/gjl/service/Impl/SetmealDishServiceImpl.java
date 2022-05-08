package com.gjl.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjl.domain.Setmeal;
import com.gjl.domain.SetmealDish;
import com.gjl.mapper.SetmealDishMapper;
import com.gjl.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
