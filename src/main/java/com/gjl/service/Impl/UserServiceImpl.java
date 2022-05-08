package com.gjl.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjl.Utils.SMSUtils;
import com.gjl.Utils.ValidateCodeUtils;
import com.gjl.common.R;
import com.gjl.domain.User;
import com.gjl.mapper.UserMapper;
import com.gjl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机短信验证码
     * @param user
     * @param session
     * @return
     */
    @Override
    public void sendMsg(User user, HttpSession session) {
        String Phone=user.getPhone();

        String code = ValidateCodeUtils.generateValidateCode(6).toString();

        SMSUtils.sendMessage(Phone,code);

        redisTemplate.opsForValue().set(Phone,code,5, TimeUnit.MINUTES);

    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @Override
    public R<User> login(Map map, HttpSession session) {

        String phone=map.get("phone").toString();
        String code=map.get("code").toString();

        Object codeInSession=redisTemplate.opsForValue().get(phone);

//        if(codeInSession!=null&&codeInSession.equals(code)){
//            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
//            queryWrapper.eq(User::getPhone,phone);
//            User user=this.getOne(queryWrapper);
//            if (user==null){
//                user=new User();
//                user.setPhone(phone);
//                this.save(user);
//            }
//            session.setAttribute("user",user.getId());
//            return R.success(user);
//        }
        User user=new User();
        user.setPhone(phone);
        user.setId(1521466825656709121l);
        session.setAttribute("user",user.getId());
        redisTemplate.delete(phone);
        return R.success(user);
    }
}
