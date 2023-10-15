package com.zx.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author KuiChi
 * @date 2023/6/7 16:34
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    SUCCESS(0, "SUCCESS"),
    BUSINESS_EXCEPTION(2000000, "BUSINESS_EXCEPTION"),
    BAD_PARAMS(3000000, "BAD_PARAMS"),
    SYSTEM_EXCEPTION(4000000, "SYSTEM_EXCEPTION")
    ;

    private final int code;
    private final String desc;

}
