package com.gjl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjl.common.R;
import com.gjl.domain.Employee;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService extends IService<Employee> {

    public R<Object> login(HttpServletRequest request,Employee employee);

    public R<Object> logout(HttpServletRequest request);

    public R<Object> saves(HttpServletRequest request,Employee employee);

    public R<Page> GetAll(int page,int pageSize,String name);

    public R<String> Update(HttpServletRequest request,Employee employee);

}
