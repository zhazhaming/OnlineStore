package com.example.onlinestore.config;

import com.example.onlinestore.filter.AdminFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 管理员校验配置
 */
@Configuration
public class AdminFilterConfig {

    @Bean
    public AdminFilter adminFilter(){
        return new AdminFilter ();
    }

    //将过滤器配置到全局链路
    @Bean(name = "adminFilterConf")
    public FilterRegistrationBean adminFilterConfig(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter (adminFilter ());
        filterRegistrationBean.addUrlPatterns ("/admin/category/*");
        filterRegistrationBean.addUrlPatterns ("/admin/product/*");
        filterRegistrationBean.addUrlPatterns ("/admin/order/*");
        filterRegistrationBean.setName ("adminFilterConf");
        return filterRegistrationBean;
    }

}
