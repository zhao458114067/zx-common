package com.zx.common.rpc.plugin;

import java.util.Map;

/**
 * @author ZhaoXu
 * @date 2022/6/15 15:20
 */
public interface RequestConfig {
    /**
     * 请求前同意配置类
     * @param headers
     * @param url
     * @param requestBody
     */
    void invoke(Map<String, Object> headers, String url, Object requestBody);
}
