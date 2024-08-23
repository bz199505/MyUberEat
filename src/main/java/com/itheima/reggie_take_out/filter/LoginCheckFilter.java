package com.itheima.reggie_take_out.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie_take_out.common.BaseContext;
import com.itheima.reggie_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * check if the user has finished login checking
 * **/
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        log.info("current requestURI:{}", requestURI);
        // paths do not need to be processed
        String[] urls = new String[] {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };

        if (check(urls, requestURI)) {
            log.info("passed current requestURI:{}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // check whether if already logged in
        // if true, pass it
        if (request.getSession().getAttribute("employee") != null) {
            log.info("loged in user's id is :{}", request.getSession().getAttribute("employee"));
            Object empId = request.getSession().getAttribute("employee");
            BaseContext.setCurrentId((Long) empId);
            filterChain.doFilter(request, response);
            return;
        }

        log.info("user not logged in");
        // not logged in
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * check matching of my urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)) {
                return true;
            }
        }

        return false;
    }
}
