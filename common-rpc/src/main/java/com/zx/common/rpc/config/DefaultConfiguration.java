package com.zx.common.rpc.config;

import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author ZhaoXu
 * @date 2023/11/7 16:24
 */
@Configuration
public class DefaultConfiguration implements RequestConfig {
    @Override
    public void invoke(Map<String, Object> headers, String url, Object requestBody) {
        headers.put("Content-Type", "application/json");
    }
}
