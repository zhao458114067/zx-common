package com.zx.common.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author ZhaoXu
 * @date 2023/10/15 15:30
 */
public class BaseConverter {
    public static <S, T> T copy(S source, Class<T> targetClass) {
        if (ObjectUtils.anyNull(source, targetClass)) {
            return null;
        }
        try {
            T t = targetClass.newInstance();
            BeanUtils.copyProperties(source, t);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <S, T> List<T> copyList(List<S> source, Class<T> targetClass) {
        return Optional.ofNullable(source).orElse(Collections.emptyList())
                .stream()
                .map(item -> copy(item, targetClass))
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toList());
    }
}
