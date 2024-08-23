package com.itheima.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_take_out.common.CustomException;
import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.entity.Category;
import com.itheima.reggie_take_out.entity.Dish;
import com.itheima.reggie_take_out.entity.Setmeal;
import com.itheima.reggie_take_out.mapper.CategoryMapper;
import com.itheima.reggie_take_out.service.CategoryService;
import com.itheima.reggie_take_out.service.DishService;
import com.itheima.reggie_take_out.service.MealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private MealService mealService;
    /**
     * remove according to id and before remove we need to judge
     * @param id
     */
    @Override
    public void remove(Long id) {
        // query current if such category has linked to dish or meal, if yes, throw error
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // query according to category id
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(dishLambdaQueryWrapper);
        if (count > 0) {
            // has link
            throw new CustomException("this category has linked dish");
        }

        LambdaQueryWrapper<Setmeal> mealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        mealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count1 = mealService.count(mealLambdaQueryWrapper);
        if (count1 > 0) {
            // has link
            throw new CustomException("this category has linked meal");
        }

        // nothing wrong, delete it
        super.removeById(id);
    }

}
