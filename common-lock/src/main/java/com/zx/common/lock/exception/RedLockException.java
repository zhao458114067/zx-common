package com.zx.common.lock.exception;

import com.zx.common.base.exception.BaseException;

/**
 * @author ZhaoXu
 * @date 2022/5/15 22:19
 */
public class RedLockException extends BaseException {
    private static final long serialVersionUID = -3284805116892805544L;

    public RedLockException(String message) {
        super(message);
    }
}
