package com.dg.mall.system.exception;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

//@Configuration
public class MyBeanHandler implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private static ConcurrentHashMap map = new ConcurrentHashMap();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        MyExceptionEnum e = applicationContext.getBean(MyExceptionEnum.class);
        Class<? extends MyExceptionEnum> clazz = e.getClass();
        Object[] enums = clazz.getEnumConstants();
        try {
            Method getCode = clazz.getMethod("getCode");
            Method getMsg = clazz.getMethod("getMsg");
            for (Object obj : enums){
                map.put(getCode.invoke(obj),getMsg.invoke(obj));
            }
        } catch (Exception exception){
            exception.printStackTrace();
        }

        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
//        genericBeanDefinition.setBeanClass();
//        beanDefinitionRegistry.registerBeanDefinition("myBeanHandler",);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
