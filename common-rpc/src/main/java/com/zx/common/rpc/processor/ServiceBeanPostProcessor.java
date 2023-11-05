package com.zx.common.rpc.processor;

import com.zx.common.base.utils.SpringManager;
import com.zx.common.rpc.annotation.RequestClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author ZhaoXu
 * @date 2023/11/2 15:04
 */
@Configuration
@Slf4j
public class ServiceBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, @Nullable String beanName) throws BeansException {
        RequestClient requestClient = bean.getClass().getAnnotation(RequestClient.class);
        if (ObjectUtils.isNotEmpty(requestClient)) {
            String value = requestClient.value();
            log.info("获取 requestClient 配置地址，beanName：{}，url：{}", beanName, value);
            if (ObjectUtils.isNotEmpty(value)) {
                InvocationHandler invocationHandler = Proxy.getInvocationHandler(requestClient);
                try {
                    Field memberValues = invocationHandler.getClass().getDeclaredField("memberValues");
                    memberValues.setAccessible(Boolean.TRUE);
                    Map memberMap = (Map) memberValues.get(invocationHandler);
                    String property = SpringManager.getApplicationContext().getEnvironment().getProperty(value);
                    if (ObjectUtils.isNotEmpty(property)) {
                        memberMap.put("domains", property.split(","));
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }
}

