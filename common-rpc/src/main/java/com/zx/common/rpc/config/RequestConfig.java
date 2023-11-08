package com.zx.common.rpc.config;

import com.zx.common.rpc.dto.RequestClientDTO;

import java.util.Map;

/**
 * @author ZhaoXu
 * @date 2022/6/15 15:20
 */
public interface RequestConfig {
    /**
     * 请求前同意配置类
     * @param requestClientDTO
     */
    void invoke(RequestClientDTO requestClientDTO);
}
