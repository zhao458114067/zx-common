package com.zx.common.base.utils;

import org.apache.commons.lang3.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author ZhaoXu
 * @date 2022/9/22 22:12
 */
@SuppressWarnings("unchecked")
public class ReflectUtils {
    /**
     * 参数转换 （支持：Byte、Boolean、String、Short、Integer、Long、Float、Double、Date）
     *
     * @param field
     * @param value
     * @return Object
     */
    public static Object convertFieldType(Field field, String value) {
        Class<?> fieldType = field.getType();

        if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType fieldGenericType = (ParameterizedType) field.getGenericType();
            if (fieldGenericType.getRawType().equals(List.class)) {
                Type type = fieldGenericType.getActualTypeArguments()[0];
                fieldType = (Class<?>) type;
            }
        }
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        value = value.trim();
        if (Byte.class.equals(fieldType) || Byte.TYPE.equals(fieldType)) {
            return Byte.valueOf(value);
        } else if (Boolean.class.equals(fieldType) || Boolean.TYPE.equals(fieldType)) {
            return Boolean.valueOf(value);
        } else if (String.class.equals(fieldType)) {
            return value;
        } else if (Short.class.equals(fieldType) || Short.TYPE.equals(fieldType)) {
            return Short.valueOf(value);
        } else if (Integer.class.equals(fieldType) || Integer.TYPE.equals(fieldType)) {
            return Integer.valueOf(value);
        } else if (Long.class.equals(fieldType) || Long.TYPE.equals(fieldType)) {
            return Long.valueOf(value);
        } else if (Float.class.equals(fieldType) || Float.TYPE.equals(fieldType)) {
            return Float.valueOf(value);
        } else if (Double.class.equals(fieldType) || Double.TYPE.equals(fieldType)) {
            return Double.valueOf(value);
        } else {
            throw new RuntimeException("类型解析错误, type=" + fieldType);
        }
    }

    public static <T> T getAnnotationFieldValue(Annotation annotation, String fieldName) {
        Map<String, Object> annotationMemberMap = getAnnotationMemberMap(annotation);
        return (T) annotationMemberMap.get(fieldName);
    }

    public static void setAnnotationFieldValue(Annotation annotation, String fieldName, Object value) {
        Map<String, Object> annotationMemberMap = getAnnotationMemberMap(annotation);
        annotationMemberMap.put(fieldName, value);
    }

    private static Map<String, Object> getAnnotationMemberMap(Annotation annotation) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        try {
            Field memberValues = invocationHandler.getClass().getDeclaredField("memberValues");
            memberValues.setAccessible(Boolean.TRUE);
            return (Map<String, Object>) memberValues.get(invocationHandler);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
