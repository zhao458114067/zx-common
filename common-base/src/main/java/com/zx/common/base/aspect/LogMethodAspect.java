package com.zx.common.base.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zx.common.base.annotation.LogAround;
import com.zx.common.base.enums.ErrorCode;
import com.zx.common.base.exception.BaseException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhaoXu
 * @date 2022/6/4 16:13
 */
@Component
@Aspect
@Slf4j
public class LogMethodAspect {
    private static final String DEFAULT_SERVICE_STR = "Service";

    private static final ClassPool CLASS_POOL = ClassPool.getDefault();

    private static Field DETAIL_MESSAGE_FIELD;

    static {
        try {
            DETAIL_MESSAGE_FIELD = Throwable.class.getDeclaredField("detailMessage");
            DETAIL_MESSAGE_FIELD.setAccessible(Boolean.TRUE);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(logAround)")
    public Object doAround(ProceedingJoinPoint joinPoint, LogAround logAround) {
        Signature signature = joinPoint.getSignature();
        Map<String, Object> paramNameAndValue = getParamNameAndValue(joinPoint);

        // 类名
        String fullClassName = signature.getDeclaringTypeName();

        // 方法名
        String methodName = signature.getName();
        String fullPath = fullClassName + "#" + methodName + " ";

        Object proceed = null;
        long startTime = System.currentTimeMillis();
        try {
            proceed = joinPoint.proceed();
            // com.xxx.TestServiceImpl#testMethod execute success, params:{}, response:{}
            log.info(fullPath + "execute success, params:{}, response:{}", objectMapper.valueToTree(paramNameAndValue),
                    objectMapper.valueToTree(proceed));
            log.info(fullPath + "execute time:{}", (System.currentTimeMillis() - startTime) + " millisecond");
        } catch (Throwable throwable) {
            // com.xxx.TestServiceImpl#testMethod execute error, params:{}
            log.error(fullPath + "execute error, params:{}", objectMapper.valueToTree(paramNameAndValue), throwable);
            throw getException(fullClassName, logAround, throwable);
        }
        return proceed;
    }

    private static String getReturnClassType(Signature signature) {
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            // 返回类型
            Class<?> methodReturnType = method.getReturnType();
            return methodReturnType.getName();
        }
        return "";
    }

    private BaseException getException(String fullClassName, LogAround logAround, Throwable throwable) {
        BaseException businessException;
        try {
            String subClassName = "";
            if (fullClassName.contains(DEFAULT_SERVICE_STR)) {
                int service = fullClassName.toLowerCase().lastIndexOf("service");
                subClassName = fullClassName.substring(0, service) + "Exception";
            } else {
                subClassName = fullClassName + "BaseException";
            }
            Class<?> subClass;
            synchronized (CLASS_POOL) {
                try {
                    subClass = Class.forName(subClassName, true, CLASS_POOL.getClassLoader());
                } catch (ClassNotFoundException e) {
                    // 需要生成类
                    CtClass subCtClass = CLASS_POOL.makeClass(subClassName);
                    CtClass superClass = CLASS_POOL.get(BaseException.class.getName());
                    subCtClass.setSuperclass(superClass);
                    //构造函数
                    CtClass stringClass = CLASS_POOL.get(String.class.getName());
                    CtClass[] constructorParamClassArr = new CtClass[]{CtClass.intType, stringClass, CLASS_POOL.get(Throwable.class.getName())};
                    CtConstructor constructor = CtNewConstructor.make(constructorParamClassArr, null, subCtClass);
                    subCtClass.addConstructor(constructor);
                    subCtClass.toClass();
                    subCtClass.detach();
                    subClass = Class.forName(subClassName, true, CLASS_POOL.getClassLoader());
                }
            }
            DETAIL_MESSAGE_FIELD.set(throwable, logAround.detailMessage());
            businessException = (BaseException) subClass.getConstructor(int.class, String.class, Throwable.class).newInstance(ErrorCode.BUSINESS_EXCEPTION.getCode(), logAround.detailMessage(), throwable);
        } catch (Exception ex) {
            log.error("getException error", ex);
            businessException = new BaseException(ErrorCode.BUSINESS_EXCEPTION.getCode(), ErrorCode.BUSINESS_EXCEPTION.getDesc(), throwable);
        }
        return businessException;
    }

    /**
     * 获取参数Map集合
     *
     * @param joinPoint
     * @return
     */
    private Map<String, Object> getParamNameAndValue(ProceedingJoinPoint joinPoint) {
        Map<String, Object> param = new HashMap<>(8);
        Object[] paramValues = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();

        for (int i = 0; i < paramNames.length; i++) {
            param.put(paramNames[i], paramValues[i]);
        }
        return param;
    }

}
