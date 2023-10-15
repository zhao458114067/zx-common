package com.zx.common.base.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author ZhaoXu
 * @date 2022/6/13 14:49
 */
public class AopHelper {
    private static final Logger log = LoggerFactory.getLogger(AopHelper.class);
    private static final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    public static String parseAnnotationText(JoinPoint joinPoint, String text) throws NoSuchMethodException {
        Assert.hasLength(text, "lock.key() cannot be empty");
        if (SpelUtils.isRawExpression(text)) {
            return text;
        } else {
            Method targetMethod = getTargetMethod(joinPoint);
            EvaluationContext context = new MethodBasedEvaluationContext(joinPoint.getTarget(), targetMethod, joinPoint.getArgs(), nameDiscoverer);
            return String.valueOf(SpelUtils.parseExpression(text, context));
        }
    }

    public static <T extends Annotation> T getClassAnnotation(JoinPoint joinPoint, Class<T> annotation) {
        try {
            Class<?> targetClass = getTargetClass(joinPoint.getTarget());
            return targetClass.getAnnotation(annotation);
        } catch (Exception e) {
            log.error("Fail to find annotation={}, joinPoint={}, message={}", annotation.getSimpleName(), joinPoint, e.getMessage());
            return null;
        }
    }

    public static <T extends Annotation> T getMethodAnnotation(JoinPoint joinPoint, Class<T> annotation) {
        try {
            Method method = getTargetMethod(joinPoint);
            return method.getAnnotation(annotation);
        } catch (Exception e) {
            log.error("Fail to find annotation={}, joinPoint={}, message={}", annotation.getSimpleName(), joinPoint, e.getMessage());
            return null;
        }
    }

//    public static Method getInternalFallbackMethod(JoinPoint joinPoint, boolean mustStatic, String fallbackMethodName, Class<? extends Exception> eClazz) throws NoSuchMethodException {
//        Method originMethod = getTargetMethod(joinPoint);
//        Class<?>[] originParameterTypes = originMethod.getParameterTypes();
//        Class<?>[] parameterTypes = (Class[]) Arrays.copyOf(originParameterTypes, originParameterTypes.length + 1);
//        parameterTypes[parameterTypes.length - 1] = eClazz;
//        return MethodUtils.getDeclaredMethod(mustStatic, joinPoint.getTarget().getClass(), fallbackMethodName, originMethod.getReturnType(), parameterTypes);
//    }

    public static Method getTargetMethod(JoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = getTargetClass(joinPoint.getTarget());
        return targetClass.getDeclaredMethod(signature.getName(), signature.getMethod().getParameterTypes());
    }

    public static Class<?> getTargetClass(Object candidate) {
        return AopUtils.getTargetClass(candidate);
    }

    public static String simpleMethodSignature(MethodSignature methodSignature) {
        return methodSignature.getDeclaringType().getSimpleName() + "." + methodSignature.getName();
    }
}
