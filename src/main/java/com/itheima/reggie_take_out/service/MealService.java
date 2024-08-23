package com.itheima.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie_take_out.dto.SetmealDto;
import com.itheima.reggie_take_out.entity.Setmeal;

import java.util.List;

public interface MealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
}
