package com.zx.common.rpc.executor;

import com.zx.common.base.utils.HttpClientUtil;
import com.zx.common.rpc.dto.RequestClientDTO;
import java.util.Map;

/**
 * @author ZhaoXu
 * @date 2023/11/8 14:31
 */
public class HttpExecutor implements BaseExecutor {
    @Override
    public Object execute(RequestClientDTO requestClientDTO) {
        String url = requestClientDTO.getDomain() + "/" + requestClientDTO.getPath();
        Map<String, Object> headers = requestClientDTO.getHeaders();
        Object requestBody = requestClientDTO.getRequestBody();
        // 发送请求
        Object result;
        switch (requestClientDTO.getRequestMethod()) {
            case GET:
                result = HttpClientUtil.get(headers, url, Object.class);
                break;
            case POST:
                result = HttpClientUtil.post(headers, url, requestBody, Object.class);
                break;
            case PUT:
                result = HttpClientUtil.put(headers, url, requestBody, Object.class);
                break;
            case DELETE:
                result = HttpClientUtil.delete(headers, url, requestBody, Object.class);
                break;
            default:
                return null;
        }
        return result;
    }
}
