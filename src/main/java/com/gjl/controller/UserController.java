package com.gjl.controller;

import com.gjl.common.R;
import com.gjl.domain.User;
import com.gjl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        service.sendMsg(user,session);
        return R.success("发送成功，请稍等！");
    }

    /**
     * 移动端用户登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R<Object> login(@RequestBody Map map, HttpSession session){
        return R.success(service.login(map,session));
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public R<Object> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出成功！");
    }
}
