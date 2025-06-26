package com.agents.builder.common.mybatis.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.agents.builder.common.core.domain.model.LoginUser;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.mybatis.core.domain.BaseEntity;
import com.agents.builder.common.satoken.interceptor.UserContextHolderInterceptor;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MP注入处理器
 *
 * @date 2021/4/25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InjectionMetaObjectHandler implements MetaObjectHandler {

    private final UserContextHolderInterceptor userContextHolderInterceptor;

    @Override
    public void insertFill(MetaObject metaObject) {


        if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
            Date current = ObjectUtil.isNotNull(baseEntity.getCreateTime())
                    ? baseEntity.getCreateTime() : new Date();
            baseEntity.setCreateTime(current);
            baseEntity.setUpdateTime(current);
            try {
                LoginUser loginUser = getLoginUser();
                if (ObjectUtil.isNotNull(loginUser)) {
                    Long userId = ObjectUtil.isNotNull(baseEntity.getCreateBy())
                            ? baseEntity.getCreateBy() : loginUser.getUserId();
                    // 当前已登录 且 创建人为空 则填充
                    baseEntity.setCreateBy(userId);
                    // 当前已登录 且 更新人为空 则填充
                    baseEntity.setUpdateBy(userId);
                }else {
                    throw new ServiceException("自动注入异常 => ");
                }
            } catch (Exception e) {
                baseEntity.setCreateBy(0L);
                baseEntity.setUpdateBy(0L);
            }

        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                Date current = new Date();
                // 更新时间填充(不管为不为空)
                baseEntity.setUpdateTime(current);
                LoginUser loginUser = getLoginUser();
                // 当前已登录 更新人填充(不管为不为空)
                if (ObjectUtil.isNotNull(loginUser)) {
                    baseEntity.setUpdateBy(loginUser.getUserId());
                }
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    /**
     * 获取登录用户名
     */
    private LoginUser getLoginUser() {
        LoginUser loginUser;
        try {
            loginUser = LoginHelper.getLoginUser();
        } catch (Exception e) {
            loginUser = userContextHolderInterceptor.getLoginUser();
        }
        return loginUser;
    }

}
