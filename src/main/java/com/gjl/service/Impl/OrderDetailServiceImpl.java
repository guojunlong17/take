package com.gjl.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjl.domain.OrderDetail;
import com.gjl.mapper.OrderDetailMapper;
import com.gjl.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}