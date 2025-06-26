package com.agents.builder.common.core.exception;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChatLimitException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    public ChatLimitException(String message) {
        this.message = message;
    }

    public ChatLimitException(String message, Integer code) {
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

    public ChatLimitException setMessage(String message) {
        this.message = message;
        return this;
    }

}
