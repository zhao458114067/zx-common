package com.zx.common.base.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ZhaoXu
 * @date 2022/6/3 23:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = -1818626802303482513L;

    private Integer code;

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Integer code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }
}
