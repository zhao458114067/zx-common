package com.zx.common.web.response;

import com.zx.common.base.enums.ErrorCode;
import com.zx.common.base.exception.BaseException;
import com.zx.common.base.model.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @author ZhaoXu
 * @date 2022/6/13 16:47
 */
@RestControllerAdvice
@Slf4j
public class ServerExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<Object> exception(Exception e) {
        log.error(e.getMessage(), e);
        return BaseResponse.fail(e.getMessage(), ErrorCode.SYSTEM_EXCEPTION.getCode());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<Object> handleMethodArgumentsNotValid(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMessages = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" & "));
        String message = "参数校验失败: " + errorMessages;
        log.error(message, e);
        return BaseResponse.fail(message, ErrorCode.SYSTEM_EXCEPTION.getCode());
    }

    @ExceptionHandler(BaseException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public BaseResponse<Object> bizException(BaseException e) {
        log.error(e.getMessage(), e);
        return BaseResponse.fail(e.getMessage(), ErrorCode.SYSTEM_EXCEPTION.getCode());
    }
}
