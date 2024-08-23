package com.itheima.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_take_out.entity.Employee;
import com.itheima.reggie_take_out.mapper.EmployMapper;
import com.itheima.reggie_take_out.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployMapper, Employee> implements EmployeeService {

}
