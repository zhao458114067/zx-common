package com.zx.common.crawl.annotation;

import com.zx.common.crawl.enums.SelectTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ZhaoXu
 * @date 2022/9/22 17:44
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldSelect {
    String cssQuery() default "";

    SelectTypeEnum selectType() default SelectTypeEnum.TEXT;

    String selectAttr() default "";
}
