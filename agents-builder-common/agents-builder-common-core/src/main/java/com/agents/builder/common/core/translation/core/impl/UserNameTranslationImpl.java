package com.agents.builder.common.core.translation.core.impl;

import com.agents.builder.common.core.translation.annotation.TranslationType;
import com.agents.builder.common.core.translation.constant.TransConstant;
import com.agents.builder.common.core.translation.core.TranslationInterface;
import lombok.AllArgsConstructor;
import com.agents.builder.common.core.service.UserService;

/**
 * 用户名翻译实现
 *
 * @author Yang Chao
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.USER_ID_TO_NAME)
public class UserNameTranslationImpl implements TranslationInterface<String> {

    private final UserService userService;

    @Override
    public String translation(Object key, String other) {
        if (key instanceof Long id) {
            return userService.selectUserNameById(id);
        }
        return null;
    }
}
