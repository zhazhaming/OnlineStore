package com.example.onlinestore.filter;


import com.example.onlinestore.common.Constant;
import com.example.onlinestore.model.pojo.User;
import com.example.onlinestore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 管理员过滤器
 */
public class AdminFilter implements Filter {

    @Autowired
    UserService userService;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession ( );
        User currUser = (User) session.getAttribute (Constant.ONLINESTORE_USER);
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
        //校验是不是管理员
        boolean adminRole = userService.checkAdminRole (currUser);
        if (adminRole){
            filterChain.doFilter (servletRequest,servletResponse);
        }else {
            PrintWriter out = new HttpServletResponseWrapper ((HttpServletResponse) servletResponse).getWriter ( );
            out.write ("{\n" +
                    "    \"status\": 10009,\n" +
                    "    \"msg\": \"NEED_ADMIN\",\n" +
                    "    \"data\": null\n" +
                    "}");
            out.flush ();
            out.close ();
        }
    }

    @Override
    public void destroy() {

    }
}
