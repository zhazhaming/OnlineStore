package com.example.onlinestore.filter;

import com.example.onlinestore.common.Constant;
import com.example.onlinestore.model.pojo.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用户校验过滤器
 */
public class UserFilter implements Filter {
    public static User currUser;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession ( );
        currUser = (User) session.getAttribute (Constant.ONLINESTORE_USER);
        if (currUser == null){
            PrintWriter out = new HttpServletResponseWrapper ((HttpServletResponse) servletResponse).getWriter ( );
            out.write ("{\n" +
                    "    \"status\": 10007,\n" +
                    "    \"msg\": \"NEED_LOGIN\",\n" +
                    "    \"data\": null\n" +
                    "}");
            out.flush ();
            out.close ();
            return;
        }
        filterChain.doFilter (servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
