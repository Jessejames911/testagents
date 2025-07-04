package com.agents.builder.common.mail.utils;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

import java.io.Serial;

/**
 * 邮件异常
 *
 * @author xiaoleilu
 */
public class MailException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 8247610319171014183L;

    public MailException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public MailException(String message) {
        super(message);
    }

    public MailException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public MailException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public MailException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public MailException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
