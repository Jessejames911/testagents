package com.agents.builder.common.web.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.agents.builder.common.core.domain.R;
import com.agents.builder.common.core.exception.ChatLimitException;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.exception.StreamException;
import com.agents.builder.common.core.exception.base.BaseException;
import com.agents.builder.common.core.utils.StreamUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.util.HashMap;

/**
 * 全局异常处理器
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException.class)
    @ResponseBody
    public R<Void> handleNotPermissionException(NotPermissionException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限码校验失败'{}'", requestURI, e.getMessage());
        return R.fail(HttpStatus.HTTP_FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException.class)
    @ResponseBody
    public R<Void> handleNotRoleException(NotRoleException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',角色权限校验失败'{}'", requestURI, e.getMessage());
        return R.fail(HttpStatus.HTTP_FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 认证失败
     */
    @ExceptionHandler(NotLoginException.class)
    public void handleNotLoginException(NotLoginException e, HttpServletResponse response) {
        log.error("认证失败'{}',无法访问系统资源", e.getMessage());
        response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public R<Void> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                       HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return R.fail(e.getMessage());
    }

    /**
     * 对话异常
     */
    @ExceptionHandler(ChatLimitException.class)
    @ResponseBody
    public void handleChatLimitException(ChatLimitException e, HttpServletResponse response) throws IOException, InterruptedException {
        log.error(e.getMessage());
        response.setStatus(e.getCode());
    }


    /**
     * 流式对话异常
     */
    @ExceptionHandler(StreamException.class)
    @ResponseBody
    public void handleStreamException(StreamException e, HttpServletResponse response) throws IOException, InterruptedException {
        log.error(e.getMessage());
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("isEnd", true);
        map.put("content", e.getMessage());

        response.getWriter().write("event:" + "message" + "\n");
        response.getWriter().write("data:" + JSONUtil.toJsonStr(map) + "\n\n");

        response.getWriter().flush();

        response.setStatus(200);
        response.getWriter().close();
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public R<Void> handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error(e.getMessage());
        Integer code = e.getCode();
        return ObjectUtil.isNotNull(code) ? R.fail(code, e.getMessage()) : R.fail(e.getMessage());
    }


    /**
     * 业务异常
     */
    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public R<Void> handleBaseException(BaseException e, HttpServletRequest request) {
        log.error(e.getMessage());
        return R.fail(e.getMessage());
    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseBody
    public R<Void> handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestURI);
        return R.fail(String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()));
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public R<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestURI);
        return R.fail(String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), e.getRequiredType().getName(), e.getValue()));
    }



    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public R<Void> handleBindException(BindException e) {
        log.error(e.getMessage());
        String message = StreamUtils.join(e.getAllErrors(), DefaultMessageSourceResolvable::getDefaultMessage, ", ");
        return R.fail(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public R<Void> constraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage());
        String message = StreamUtils.join(e.getConstraintViolations(), ConstraintViolation::getMessage, ", ");
        return R.fail(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public R<Void> illegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return R.fail(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return R.fail(message);
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public R<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return R.fail("该功能正在维护中，请稍后再试...");
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return R.fail("该功能正在维护中，请稍后再试...");
    }

}
