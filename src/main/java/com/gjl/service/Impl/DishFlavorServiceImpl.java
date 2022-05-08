package com.gjl.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjl.domain.DishFlavor;
import com.gjl.mapper.DishFlavorMapper;
import com.gjl.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService {
}
