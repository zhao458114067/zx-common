package com.zx.common.rpc.registry;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @author ZhaoXu
 */
public class SendClientFactoryBean implements FactoryBean<Object> {
    private final Class<Object> mapperInterface;

    public SendClientFactoryBean(Class<Object> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object getObject() {
        SendClientHandler<Object> testHandler = new SendClientHandler<>(mapperInterface);
        return Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, testHandler);
    }

    @Override
    public Class<Object> getObjectType() {
        return mapperInterface;
    }
}
