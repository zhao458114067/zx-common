package com.zx.common.rpc.config;

import com.zx.common.rpc.dto.RequestClientDTO;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhaoXu
 * @date 2023/11/7 16:24
 */
@Configuration
public class DefaultConfiguration implements RequestConfig {
    @Override
    public void invoke(RequestClientDTO requestClientDTO) {
        Map<String, Object> headers = new HashMap<>(8);
        headers.put("Content-Type", "application/json");
        requestClientDTO.setHeaders(headers);
    }
}
