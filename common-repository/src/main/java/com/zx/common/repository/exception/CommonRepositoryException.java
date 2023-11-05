package com.zx.common.repository.exception;

import com.zx.common.base.exception.BaseException;
import lombok.Getter;

/**
 * @author ZhaoXu
 * @date 2023/11/5 11:23
 */
@Getter
public class CommonRepositoryException extends BaseException {
    private static final long serialVersionUID = 1172953538811667736L;

    public CommonRepositoryException(String message) {
        super(message);
    }
}
