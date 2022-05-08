package com.gjl.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjl.common.R;
import com.gjl.domain.Employee;
import com.gjl.mapper.EmployeeMapper;
import com.gjl.service.EmployeeService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {


    @Override
    public R<Object> login(HttpServletRequest request, Employee employee) {
        //解密密码
        String password=employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        //查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = super.getOne(queryWrapper);

        if (emp==null){
            return R.error("账号错误！");
        }

        if (!emp.getPassword().equals(password)){
            return R.error("密码错误！");
        }

        if (emp.getStatus()==0){
            return R.error("账号已禁用");
        }
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }

    @Override
    public R<Object> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    @Override
    public R<Object> saves(HttpServletRequest request,Employee employee) {

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long id=(Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);
        super.save(employee);
        return R.success("添加成功！");
    }

    @Override
    public R<Page> GetAll(int page, int pageSize, String name) {

        Page<Employee> pageInfo=new Page<Employee>(page,pageSize);
        LambdaQueryWrapper<Employee> qw=new LambdaQueryWrapper<Employee>();
        qw.like(Strings.isNotEmpty(name),Employee::getName,name);
        qw.orderByDesc(Employee::getUpdateTime);

        super.page(pageInfo,qw);

        return R.success(pageInfo);
    }

    @Override
    public R<String> Update(HttpServletRequest request, Employee employee) {

//        Long id=(Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(id);
        super.updateById(employee);
        return R.success("修改成功！");
    }
}
