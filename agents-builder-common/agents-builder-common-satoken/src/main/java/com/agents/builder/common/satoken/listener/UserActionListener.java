package com.agents.builder.common.satoken.listener;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.agents.builder.common.redis.utils.RedissonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.agents.builder.common.core.constant.CacheConstants;
import com.agents.builder.common.core.domain.dto.UserOnlineDTO;
import com.agents.builder.common.core.domain.model.LoginUser;
import com.agents.builder.common.core.enums.UserType;
import com.agents.builder.common.core.utils.ServletUtils;
import com.agents.builder.common.core.utils.ip.AddressUtils;
import com.agents.builder.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 用户行为 侦听器的实现
 *
 * @author Angus
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class UserActionListener implements SaTokenListener {

    private final SaTokenConfig tokenConfig;

    /**
     * 每次登录时触发
     */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
        LoginUser user = LoginHelper.getLoginUser();

        if (UserType.SYS_USER.getUserType().equals(user.getUserType())) {
            UserAgent userAgent = UserAgentUtil.parse(ServletUtils.getRequest().getHeader("User-Agent"));
            String ip = ServletUtils.getClientIP();

            UserOnlineDTO dto = new UserOnlineDTO();
            dto.setIpaddr(ip);
            dto.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
            dto.setBrowser(userAgent.getBrowser().getName());
            dto.setOs(userAgent.getOs().getName());
            dto.setLoginTime(System.currentTimeMillis());
            dto.setTokenId(tokenValue);
            dto.setUserName(user.getUsername());
            dto.setClientKey(user.getClientKey());
            dto.setDeviceType(user.getDeviceType());

            if(tokenConfig.getTimeout() == -1) {
                RedissonUtils.setCacheObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue, dto);
            } else {
                RedissonUtils.setCacheObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue, dto, Duration.ofSeconds(tokenConfig.getTimeout()));
            }
            log.info("user doLogin, userId:{}, token:{}", loginId, tokenValue);
        } else if (UserType.APP_USER.getUserType().equals(user.getUserType()) ) {
            // app端 自行根据业务编写
        }
    }

    /**
     * 每次注销时触发
     */
    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        RedissonUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue);
        log.info("user doLogout, userId:{}, token:{}", loginId, tokenValue);
    }

    /**
     * 每次被踢下线时触发
     */
    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        RedissonUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue);
        log.info("user doKickout, userId:{}, token:{}", loginId, tokenValue);
    }

    /**
     * 每次被顶下线时触发
     */
    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {
        RedissonUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue);
        log.info("user doReplaced, userId:{}, token:{}", loginId, tokenValue);
    }

    /**
     * 每次被封禁时触发
     */
    @Override
    public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
    }

    /**
     * 每次被解封时触发
     */
    @Override
    public void doUntieDisable(String loginType, Object loginId, String service) {
    }

    /**
     * 每次打开二级认证时触发
     */
    @Override
    public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
    }

    /**
     * 每次创建Session时触发
     */
    @Override
    public void doCloseSafe(String loginType, String tokenValue, String service) {
    }

    /**
     * 每次创建Session时触发
     */
    @Override
    public void doCreateSession(String id) {
    }

    /**
     * 每次注销Session时触发
     */
    @Override
    public void doLogoutSession(String id) {
    }

    /**
     * 每次Token续期时触发
     */
    @Override
    public void doRenewTimeout(String tokenValue, Object loginId, long timeout) {
    }
}
