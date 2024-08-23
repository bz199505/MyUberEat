package com.itheima.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.entity.Employee;
import com.itheima.reggie_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // md5 pwd
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // query db and check
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // checking
        if (emp == null) {
            return R.error("login failed 1");
        }
        if (!password.equals(emp.getPassword())) {
            return R.error("login failed 2");
        }
        if (emp.getStatus() == 0) {
            return R.error("account banned");
        }

        // put into session
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
     }

     @PostMapping("/logout")
    public R logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");

        return R.success("logout success");
    }

    /**
     * add employee
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("new employee : {}", employee.toString());

        // give a default pwd with md5 encrypted 123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        // save user
        employeeService.save(employee);
        return R.success("save(new user) success");
    }

    /**
     * pagination of employee page
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        // build pagination
        Page pageInfo = new Page(page, pageSize);
        // condition query
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getUsername, name);
        // sorting condition
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * update employee info according to id
     * @param employee
     * @return String
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee) {
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        log.info("empid {}", empId);
//
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);

        employeeService.updateById(employee);
        return R.success("update(new user) success");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("query employee info");
        Employee employee = employeeService.getById(id);

        if (employee != null) {
            return R.success(employee);
        }

        return R.error("employee not exist");
    }
}
