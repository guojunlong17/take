package com.gjl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gjl.domain.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
