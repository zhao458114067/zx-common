package com.zx.common.rpc.annotation;

import com.zx.common.rpc.plugin.DefaultConfiguration;
import org.springframework.stereotype.Service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ZhaoXu
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
@Documented
public @interface RequestClient {
    /**
     * spring环境变量
     *
     * @return
     */
    String value() default "";

    /**
     * 请求域名
     *
     * @return
     */
    String[] domains() default {"http://127.0.0.1:8080"};

    /**
     * 配置类
     * @return
     */
    Class<?> config() default DefaultConfiguration.class;
}
