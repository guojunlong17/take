package com.gjl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gjl.domain.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
