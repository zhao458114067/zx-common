package com.zx.common.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ZhaoXu
 * @date 2022/4/15 16:18
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAround {
    /**
     * 执行异常message
     * @return
     */
    String detailMessage() default "业务执行异常";
}
