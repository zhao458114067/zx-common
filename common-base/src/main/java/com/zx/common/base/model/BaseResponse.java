package com.zx.common.base.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ZhaoXu
 * @date 2022/9/21 18:02
 */
@Data
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 3033064992873838459L;

    private final int code;

    private final String message;

    private T data;

    public BaseResponse(T data) {
        this.code = 0;
        this.message = "success";
        this.data = data;
    }

    public BaseResponse() {
        this.code = 0;
        this.message = "success";
    }

    public BaseResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponse<T> fail(String message, Integer code) {
        return new BaseResponse<>(code, message, null);
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(data);
    }

    public static BaseResponse<Void> successVoid() {
        return new BaseResponse<>();
    }
}
