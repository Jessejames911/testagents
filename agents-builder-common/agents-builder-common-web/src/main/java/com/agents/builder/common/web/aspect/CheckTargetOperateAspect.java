package com.agents.builder.common.web.aspect;


import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.SaTokenException;
import com.agents.builder.common.core.domain.model.LoginUser;
import com.agents.builder.common.core.service.DataScopeService;
import com.agents.builder.common.satoken.annotation.CheckTargetOperate;
import com.agents.builder.common.satoken.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.Set;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class CheckTargetOperateAspect {

    @Qualifier("teamMemberPermissionServiceImpl")
    @Autowired
    private DataScopeService dataScopeService;

    @Before(value = "@annotation(checkTargetOperate)")
    public void boBefore(JoinPoint joinPoint, CheckTargetOperate checkTargetOperate) {
        if (LoginHelper.isSuperAdmin()){
            return;
        }

        Set<String> permissions = dataScopeService.getAllPermissions(LoginHelper.getUserId());

        for (String target : checkTargetOperate.value()) {
            String permission = checkTargetOperate.targetType().getKey() + ":" + checkTargetOperate.operateType().getKey() + ":" + parseEl(target,joinPoint);
            if (!permissions.contains(permission)){
                throw new NotPermissionException("没有操作权限");
            }
        }

    }

    private String parseEl(String value, JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Parameter[] parameters = signature.getMethod().getParameters();

        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(value);
        // 开始准备表达式运行环境
        EvaluationContext ctx = new StandardEvaluationContext();
        // 填充表达式上下文环境
        for(int i=0;i<parameters.length;i++){
            ctx.setVariable(parameters[i].getName(),args[i]);
        }
        return expression.getValue(ctx, String.class);
    }
}
