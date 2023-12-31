package com.zx.common.rpc.annotation;

import com.zx.common.rpc.proxy.RequestClientsRegistry;
import org.springframework.context.annotation.Import;

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
@Documented
@Import({RequestClientsRegistry.class})
public @interface EnableHttpRequest {
}
