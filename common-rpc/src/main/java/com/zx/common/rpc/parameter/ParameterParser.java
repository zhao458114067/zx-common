package com.zx.common.rpc.parameter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zx.common.base.utils.JsonUtils;
import com.zx.common.rpc.dto.RequestClientDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**
 * @author ZhaoXu
 * @date 2023/11/8 13:22
 */
public class ParameterParser {
    public static void setParameters(Method method, Object[] args, RequestClientDTO requestClientDTO) {
        String path = requestClientDTO.getPath();
        StringBuilder pathBuilder = new StringBuilder((path.endsWith("?") ? path : (path + "?")));
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                if (Map.class.isAssignableFrom(parameter.getType())) {
                    ((Map<String, Object>) args[i]).forEach((k, v) -> {
                        pathBuilder.append(k).append("=").append(urlEncode(v)).append("&");
                    });
                } else {
                    pathBuilder.append(requestParam.value()).append("=").append(urlEncode(args[i])).append("&");
                }
            } else if (parameter.isAnnotationPresent(PathVariable.class)) {
                PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
                String paramName = ObjectUtils.isNotEmpty(pathVariable.value()) ? pathVariable.value() : parameter.getName();
                String replaceStr = "{" + paramName + "}";
                int replaceStart = replaceStr.indexOf(replaceStr);
                int replaceEnd = replaceStart + replaceStr.length();
                pathBuilder.replace(replaceStart, replaceEnd, urlEncode(args[i]));
            } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                requestClientDTO.setRequestBody(args[i]);
            }
        }
        requestClientDTO.setPath(pathBuilder.toString());
    }

    private static String urlEncode(Object text) {
        try {
            text = URLEncoder.encode(Optional.ofNullable(text).map(String::valueOf).orElse(""), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            text = "";
        }
        return (String) text;
    }
}
