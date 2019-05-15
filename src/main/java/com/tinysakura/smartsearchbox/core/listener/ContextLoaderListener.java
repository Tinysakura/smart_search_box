package com.tinysakura.smartsearchbox.core.listener;

import com.tinysakura.smartsearchbox.core.Launch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/15
 */
@Slf4j
public class ContextLoaderListener extends ContextLoader implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());

        log.info("容器初始化完成，开始处理@index注解");
        Launch launch = webApplicationContext.getBean(Launch.class);
        launch.indexAnnotationProcessor();
    }
}