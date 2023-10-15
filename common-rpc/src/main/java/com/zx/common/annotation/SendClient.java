package com.zx.common.annotation;

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
public @interface SendClient {
    /**
     * 请求域名，设置多个可负载均衡
     *
     * @return
     */
    String[] value() default "http://localhost:8080";
}
