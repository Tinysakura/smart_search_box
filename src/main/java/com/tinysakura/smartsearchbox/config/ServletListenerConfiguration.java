package com.tinysakura.smartsearchbox.config;

import com.tinysakura.smartsearchbox.core.listener.ContextLoaderListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContextListener;

/**
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/15
 */
@Configuration
public class ServletListenerConfiguration {

    @Bean
    public ServletListenerRegistrationBean<ServletContextListener> getDemoListener(){
        ServletListenerRegistrationBean<ServletContextListener> registrationBean
                =new ServletListenerRegistrationBean<>();
        registrationBean.setListener(new ContextLoaderListener());
        return registrationBean;
    }
}