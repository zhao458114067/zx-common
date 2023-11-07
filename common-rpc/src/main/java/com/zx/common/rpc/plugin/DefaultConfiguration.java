package com.zx.common.rpc.plugin;

import java.util.Map;

/**
 * @author ZhaoXu
 * @date 2023/11/7 16:24
 */
public class DefaultConfiguration implements RequestConfig {
    @Override
    public void invoke(Map<String, Object> headers, String url, Object requestBody) {
        headers.put("Content-Type", "application/json");
    }
}
