package com.yilers.jwtp.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @program: JwtPermission
 * @description:
 * @author: hui.zhang
 * @date: 2021/1/18 5:10 下午
 **/

@Component
@Lazy(false)
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext APPLICATIONCONTEXT;

    /**
     * 设置spring上下文
     * @param applicationContext spring上下文
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATIONCONTEXT = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return APPLICATIONCONTEXT;
    }

    public static <T> T getBean(Class<T> clazz) {
        return APPLICATIONCONTEXT.getBean(clazz);
    }
}
