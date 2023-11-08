package com.zx.common.rpc.loadblance;

import com.zx.common.rpc.constant.RpcConstants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ZhaoXu
 * @date 2023/11/8 14:19
 */
public class LoadBalancer {

    private static final Map<String, AtomicInteger> LOAD_BALANCE_MAP = new ConcurrentHashMap<>();

    /**
     * 获取负载均衡域名地址
     * @param interfaceName
     * @param domains
     * @return
     */
    public static String getDomain(String interfaceName, String[] domains) {
        int random = LOAD_BALANCE_MAP.computeIfAbsent(interfaceName, (key) -> new AtomicInteger(-1)).incrementAndGet();
        String domain = domains[random % domains.length];
        if (!domain.startsWith(RpcConstants.HTTP) && !domain.startsWith(RpcConstants.HTTPS)) {
            domain = RpcConstants.HTTP + domain;
        }
        return domain;
    }
}
