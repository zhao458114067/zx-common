package com.zx.common.plugin;

import java.util.Map;

/**
 * @author ZhaoXu
 * @date 2023/10/15 15:20
 */
public interface BeforeSendPlugin {
    void invoke(Map<String, Object> headers, String url, Object requestBody);
}
