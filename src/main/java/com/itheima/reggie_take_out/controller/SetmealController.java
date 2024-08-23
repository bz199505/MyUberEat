package com.itheima.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.dto.SetmealDto;
import com.itheima.reggie_take_out.entity.Category;
import com.itheima.reggie_take_out.entity.Setmeal;
import com.itheima.reggie_take_out.service.CategoryService;
import com.itheima.reggie_take_out.service.MealService;
import com.itheima.reggie_take_out.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Meal management
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    private final SetmealDishService setmealDishService;
    private final MealService mealService;
    private final CategoryService categoryService;

    public SetmealController(SetmealDishService setmealDishService, MealService mealService, CategoryService categoryService) {
        this.setmealDishService = setmealDishService;
        this.mealService = mealService;
        this.categoryService = categoryService;
    }

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        mealService.saveWithDish(setmealDto);

        return R.success("new meal success");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        // like query
        wrapper.like(name != null, Setmeal::getName, name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        // query
        mealService.page(pageInfo, wrapper);

        // get categoryName
        Page<SetmealDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = null;
        if (records != null) {
            list = new ArrayList<>();
            for (Setmeal setmeal : records) {
                SetmealDto setmealDto = new SetmealDto();
                BeanUtils.copyProperties(setmeal, setmealDto);
                Long categoryId = setmeal.getCategoryId();
                Category category = categoryService.getById(categoryId);
                if (category != null) {
                    String categoryName = category.getName();

                    setmealDto.setCategoryName(categoryName);
                }
                list.add(setmealDto);
            }
            dtoPage.setRecords(list);
        }

        return R.success(dtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        mealService.removeWithDish(ids);
        return R.success("delete success");
    }
}
