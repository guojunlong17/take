package com.gjl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gjl.common.R;
import com.gjl.domain.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

public interface UserService extends IService<User> {

    public void sendMsg(User user, HttpSession session);

    public R<User> login(Map map, HttpSession session);
}
