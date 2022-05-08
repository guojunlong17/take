package com.gjl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjl.common.R;
import com.gjl.domain.Employee;
import com.gjl.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    private EmployeeService service;

    @PostMapping("/login")
    public R<Object> login(HttpServletRequest request, @RequestBody Employee employee){
        return service.login(request,employee);
    }
    @PostMapping("/logout")
    public R<Object> logout(HttpServletRequest request){
        return service.logout(request);
    }

    @PostMapping
    public R<Object> save(HttpServletRequest request,@RequestBody Employee employee){
        return service.saves(request,employee);
    }

    @GetMapping("/page")
    public R<Page> GetAll(int page,int pageSize,String name){
        return  service.GetAll(page,pageSize,name);
    }

    @PutMapping
    public R<String> Update(HttpServletRequest request,@RequestBody Employee employee){
        return service.Update(request,employee);
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        return R.success(service.getById(id));
    }



}
