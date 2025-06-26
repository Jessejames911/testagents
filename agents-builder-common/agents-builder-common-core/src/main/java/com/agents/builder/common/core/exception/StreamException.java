package com.agents.builder.common.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class StreamException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误码
     */
    private Integer code;

    public StreamException(String message) {
        this.message = message;
    }

    public StreamException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }



    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public StreamException setMessage(String message) {
        this.message = message;
        return this;
    }
}
