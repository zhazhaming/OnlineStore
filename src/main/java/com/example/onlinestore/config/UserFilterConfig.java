package com.example.onlinestore.config;

import com.example.onlinestore.filter.UserFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserFilterConfig {

    @Bean
    public UserFilter userFilter(){
        return new UserFilter ();
    }

    //将过滤器配置到全局链路
    @Bean(name = "UserFilterConfig")
    public FilterRegistrationBean adminFilterConfig(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter (userFilter ());
        filterRegistrationBean.addUrlPatterns ("/cart/*");
        filterRegistrationBean.addUrlPatterns ("/order/*");
        filterRegistrationBean.setName ("UserFilterConfig");
        return filterRegistrationBean;
    }
}
