package com.gjl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjl.common.R;
import com.gjl.domain.Dish;
import com.gjl.dto.DishDto;

import java.util.List;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);

    public R<Page> getAll(int page,int pageSize,String name);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    public void deleteDish(List<Long> ids);

    public void updateStatus(List<Long> ids,Integer status);

    public R<List<DishDto>> list(Dish dish);
}
