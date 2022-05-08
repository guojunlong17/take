package com.gjl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gjl.domain.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

}