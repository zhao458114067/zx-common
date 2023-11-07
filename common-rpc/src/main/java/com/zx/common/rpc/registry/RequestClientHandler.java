package com.zx.common.rpc.registry;

import com.fasterxml.jackson.databind.JavaType;
import com.zx.common.rpc.annotation.RequestClient;
import com.zx.common.rpc.config.RequestConfig;
import com.zx.common.base.utils.HttpClientUtil;
import com.zx.common.base.utils.JsonUtils;
import com.zx.common.base.utils.SpringManager;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ZhaoXu
 */
public class RequestClientHandler<T> implements InvocationHandler {
    private static final Map<String, AtomicInteger> loadBalanceMap = new ConcurrentHashMap<>();
    private final Class<T> mapperInterface;

    public RequestClientHandler(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InstantiationException, IllegalAccessException {
        RequestClient requestClient = mapperInterface.getAnnotation(RequestClient.class);
        String[] domains = requestClient.domains();
        if (ObjectUtils.isEmpty(domains)) {
            return RequestClientHandler.class.getName();
        }
        String[] pathArray;
        RequestMethod requestType;
        if (method.isAnnotationPresent(GetMapping.class)) {
            pathArray = method.getAnnotation(GetMapping.class).value();
            requestType = RequestMethod.GET;
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            pathArray = method.getAnnotation(PostMapping.class).value();
            requestType = RequestMethod.POST;
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            pathArray = method.getAnnotation(PutMapping.class).value();
            requestType = RequestMethod.PUT;
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            pathArray = method.getAnnotation(DeleteMapping.class).value();
            requestType = RequestMethod.DELETE;
        } else {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            pathArray = requestMapping.value();
            requestType = requestMapping.method()[0];
        }
        if (pathArray.length > 0) {
            // 解析参数
            List<String> paramList = new ArrayList<>();
            Object paramsBody = null;
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                if (parameter.isAnnotationPresent(RequestParam.class)) {
                    RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                    String name = ObjectUtils.isNotEmpty(requestParam.value()) ? requestParam.value() : parameter.getName();
                    String value = String.valueOf(args[i]);
                    paramList.add(name + "=" + value);
                } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                    paramsBody = args[i];
                }
            }
            String url = getLoadBalanceUrl(domains, pathArray[0], paramList);
            Map<String, Object> headers = new HashMap<>(8);
            Class<RequestConfig> config = (Class<RequestConfig>) requestClient.config();
            RequestConfig requestConfig = SpringManager.getBean(config);
            requestConfig.invoke(headers, url, paramsBody);
            Object result = null;
            // 发送请求
            switch (requestType) {
                case GET:
                    result = HttpClientUtil.get(headers, url, method.getReturnType());
                    break;
                case POST:
                    result = HttpClientUtil.post(headers, url, paramsBody, method.getReturnType());
                    break;
                case PUT:
                    result = HttpClientUtil.put(headers, url, paramsBody, method.getReturnType());
                    break;
                case DELETE:
                    result = HttpClientUtil.delete(headers, url, paramsBody, method.getReturnType());
                    break;
                default:
                    return proxy;
            }
            JavaType javaType = JsonUtils.getJavaType(method);
            return JsonUtils.convertObject(result, javaType);
        }
        return RequestClientHandler.class.getName();
    }

    private String getLoadBalanceUrl(String[] domains, String path, List<String> paramList) {
        if (ObjectUtils.isNotEmpty(paramList)) {
            path = (path.endsWith("?") ? path : (path + "?")) + String.join("&", paramList);
        }
        int random = loadBalanceMap.computeIfAbsent(mapperInterface.getName(), (key) -> new AtomicInteger(-1)).incrementAndGet();
        return domains[random % domains.length] + path;
    }
}
