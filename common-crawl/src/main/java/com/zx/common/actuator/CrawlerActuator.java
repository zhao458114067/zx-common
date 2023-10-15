package com.zx.common.actuator;

import com.zx.common.annotation.FieldSelect;
import com.zx.common.annotation.RootSelect;
import com.zx.common.enums.ActuatorStrategyEnum;
import com.zx.common.enums.SelectTypeEnum;
import com.zx.common.request.CrawlerRequest;
import com.zx.common.trategy.CrawlStrategy;
import com.zx.common.utils.ReflectUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author ZhaoXu
 * @date 2023/9/22 16:59
 */
public class CrawlerActuator {
    private static final Logger log = LoggerFactory.getLogger(CrawlerActuator.class);

    public static <T> T crawl(CrawlerRequest request, Class<T> clazz) {
        if (clazz == null || !Optional.ofNullable(request).map(CrawlerRequest::getUrl).isPresent()) {
            return null;
        }
        // 不同策略执行不同的加载方式
        CrawlStrategy crawlStrategy = ActuatorStrategyEnum.getInstance(request.getStrategy());
        Document document = crawlStrategy.loadPage(request);

        return parseAndMapping(document, clazz);
    }

    /**
     * 解析document并映射到实体对象当中
     *
     * @param document
     * @param clazz
     * @param <T>
     * @return
     */
    private static <T> T parseAndMapping(Document document, Class<T> clazz) {
        RootSelect rootSelect = clazz.getAnnotation(RootSelect.class);
        String rootCssQuery = (rootSelect == null || ObjectUtils.isEmpty(rootSelect.cssQuery())) ? "body" : rootSelect.cssQuery();
        Elements rootElements = document.select(rootCssQuery);
        T result = null;
        try {
            result = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        for (Element rootElement : rootElements) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                FieldSelect fieldSelect = declaredField.getAnnotation(FieldSelect.class);
                if (Objects.isNull(fieldSelect)) {
                    continue;
                }
                String fieldCssQuery = fieldSelect.cssQuery();
                if (ObjectUtils.isEmpty(fieldCssQuery)) {
                    continue;
                }
                Elements fieldElements = rootElement.select(fieldCssQuery);
                SelectTypeEnum selectTypeEnum = fieldSelect.selectType();
                String selectAttr = fieldSelect.selectAttr();
                Type genericType = declaredField.getGenericType();
                Object fieldValue = null;
                if (genericType instanceof ParameterizedType) {
                    // list类型属性
                    if (List.class.equals(((ParameterizedType) genericType).getRawType())) {
                        fieldValue = fieldElements.stream()
                                .map(fieldElement -> {
                                    String fieldString = getFieldString(fieldElement, selectTypeEnum, selectAttr);
                                    return ReflectUtils.convertFieldType(declaredField, fieldString);
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                    }
                } else {
                    if (!fieldElements.isEmpty()) {
                        Element fieldElement = fieldElements.get(0);
                        String fieldString = getFieldString(fieldElement, selectTypeEnum, selectAttr);
                        fieldValue = ReflectUtils.convertFieldType(declaredField, fieldString);
                    }
                }
                declaredField.setAccessible(Boolean.TRUE);
                try {
                    declaredField.set(result, fieldValue);
                } catch (IllegalAccessException e) {
                    log.warn("内容映射警告，class:{}, fieldName:{}, value:{}", clazz, declaredField.getName(), fieldValue);
                }
            }
        }
        return result;
    }

    /**
     * 获取页面element属性值，返回字符串
     *
     * @param fieldElement
     * @param selectType
     * @param selectAttr
     * @return
     */
    public static String getFieldString(Element fieldElement, SelectTypeEnum selectType, String selectAttr) {
        String fieldElementOrigin = null;
        if (Objects.equals(SelectTypeEnum.VAL, selectType)) {
            fieldElementOrigin = fieldElement.val();
        } else if (Objects.equals(SelectTypeEnum.TEXT, selectType)) {
            fieldElementOrigin = fieldElement.text();
        } else if (Objects.equals(SelectTypeEnum.ATTR, selectType)) {
            fieldElementOrigin = fieldElement.attr(selectAttr);
        } else {
            fieldElementOrigin = fieldElement.toString();
        }
        return fieldElementOrigin;
    }
}
