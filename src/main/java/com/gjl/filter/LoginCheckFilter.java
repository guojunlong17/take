package com.gjl.filter;

import com.alibaba.fastjson.JSON;
import com.gjl.common.BaseContext;
import com.gjl.common.R;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();

        String[] urls=new String[]{
          "/employee/login",
          "/employee/logout",
          "/backend/**",
          "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        //判断本次请求是否需要登处理
        boolean check = check(urls, requestURI);

        //如果不需要处理，则放行
        if (check){
            filterChain.doFilter(request,response);
            return;
        }

        //判断管理系统登录状态，登录了就放行
        if(request.getSession().getAttribute("employee")!=null){
            Long empId=(Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }
        //判断用户端登录状态，登录了就放行
        if(request.getSession().getAttribute("user")!=null){
            Long userId=(Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        //如果没有登录则放回未登录结果
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
    //判断请求是否与不要处理的请求一致
    public boolean check(String[] urls,String requestURL){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURL);
            if (match) return true;
        }
        return false;
    }

}
