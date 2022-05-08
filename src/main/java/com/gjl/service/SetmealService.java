package com.gjl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjl.common.R;
import com.gjl.domain.Setmeal;
import com.gjl.dto.SetmealDto;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    public R<Page> page(int page,int pageSize,String name);

    public SetmealDto getByIdWithSetmealDish(Long id);

    public void updateWithSetmealDish(SetmealDto setmealDto);

    public void updateStatus(List<Long> ids, Integer status);

    public void deleteSetmeal(List<Long> ids);

    public R<List<SetmealDto>> list(Setmeal setmeal);
}
