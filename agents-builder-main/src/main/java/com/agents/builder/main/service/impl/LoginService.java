package com.agents.builder.main.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.agents.builder.common.core.constant.Constants;
import com.agents.builder.common.core.constant.GlobalConstants;
import com.agents.builder.common.core.domain.model.LoginUser;
import com.agents.builder.common.core.enums.LoginType;
import com.agents.builder.common.core.enums.UserType;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.exception.user.UserException;
import com.agents.builder.common.core.utils.MessageUtils;
import com.agents.builder.common.core.utils.ServletUtils;
import com.agents.builder.common.core.utils.SpringUtils;
import com.agents.builder.common.redis.utils.RedissonUtils;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.agents.builder.common.web.event.LogininforEvent;
import com.agents.builder.main.domain.ApplicationAccessToken;
import com.agents.builder.main.domain.vo.UserVo;
import com.agents.builder.main.mapper.UserMapper;
import com.agents.builder.main.service.ITeamMemberPermissionService;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * 登录校验方法
 *
 * @author Angus
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class LoginService {

    @Value("${user.password.maxRetryCount}")
    private Integer maxRetryCount;

    @Value("${user.password.lockTime}")
    private Integer lockTime;

    private final ITeamMemberPermissionService permissionService;

    private final UserMapper userMapper;

    /**
     * 退出登录
     */
    public void logout() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (ObjectUtil.isNull(loginUser)) {
                return;
            }
            recordLogininfor(loginUser.getUsername(), Constants.LOGOUT, MessageUtils.message("user.logout.success"));
        } catch (NotLoginException ignored) {
        } finally {
            try {
                StpUtil.logout();
            } catch (NotLoginException ignored) {
            }
        }
    }

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     */
    public void recordLogininfor(String username, String status, String message) {
        LogininforEvent logininforEvent = new LogininforEvent();
        logininforEvent.setUsername(username);
        logininforEvent.setStatus(status);
        logininforEvent.setMessage(message);
        logininforEvent.setRequest(ServletUtils.getRequest());
        SpringUtils.context().publishEvent(logininforEvent);
    }


    /**
     * 构建登录用户
     */
    public LoginUser buildLoginUser(UserVo user, ApplicationAccessToken accessToken) {
        LoginUser old = LoginHelper.getLoginUser();
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickname(user.getNickName());
        loginUser.setRole(user.getRole());
        loginUser.setUserType(UserType.SYS_USER.getUserType());
        if (accessToken!=null) {
            loginUser.setAppId(accessToken.getApplicationId());
        }
        loginUser.setClientId(old != null && old.getClientId() != null ? old.getClientId() : IdWorker.getId());
        loginUser.setPermissions(accessToken==null?permissionService.getAllPermissions(user.getId()):permissionService.getTempPermissions(accessToken.getApplicationId()));
        return loginUser;
    }


    /**
     * 登录校验
     */
    public void checkLogin(LoginType loginType, String username, Supplier<Boolean> supplier) {
        String errorKey = GlobalConstants.PWD_ERR_CNT_KEY + username;
        String loginFail = Constants.LOGIN_FAIL;

        // 获取用户登录错误次数，默认为0 (可自定义限制策略 例如: key + username + ip)
        int errorNumber = ObjectUtil.defaultIfNull(RedissonUtils.getCacheObject(errorKey), 0);
        // 锁定时间内登录 则踢出
        if (errorNumber >= maxRetryCount) {
            recordLogininfor(username, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
            throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
        }

        if (supplier.get()) {
            // 错误次数递增
            errorNumber++;
            RedissonUtils.setCacheObject(errorKey, errorNumber, Duration.ofMinutes(lockTime));
            // 达到规定错误次数 则锁定登录
            if (errorNumber >= maxRetryCount) {
                recordLogininfor(username, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
                throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
            } else {
                // 未达到规定错误次数
                recordLogininfor(username, loginFail, MessageUtils.message(loginType.getRetryLimitCount(), errorNumber));
                throw new UserException(loginType.getRetryLimitCount(), errorNumber);
            }
        }

        // 登录成功 清空错误次数
        RedissonUtils.deleteObject(errorKey);
    }


    public String handleLogin(UserVo user, ApplicationAccessToken accessToken) {
        LoginUser loginUser = buildLoginUser(user, accessToken);

        SaLoginModel model = new SaLoginModel();
        if (accessToken != null) {
            // 应用认证 7 天有效期
            model.setTimeout(60 * 60 * 24 * 7);
            model.setExtra(Constants.TEMP_TOKEN,true);
            model.setExtra(Constants.ACCESS_TOKEN,accessToken.getAccessToken());
        }else {
            model.setExtra(Constants.TEMP_TOKEN,false);
        }

        // 生成token
        LoginHelper.login(loginUser, model);

        recordLogininfor(user.getUsername(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));

        return StpUtil.getTokenValue();
    }


    public String loginApplication(Long userId, ApplicationAccessToken accessToken) {
        UserVo userVo = userMapper.selectVoById(userId);
        Assert.notNull(userVo,"非法访问");
        if (!userVo.getIsActive()){
            throw new ServiceException("账号已被禁用");
        }
        return handleLogin(userVo,accessToken);
    }
}
