package com.agents.builder.main;

import com.agents.builder.common.core.enums.SettingType;
import com.agents.builder.common.mail.utils.MailAccount;
import com.agents.builder.common.mail.utils.MailUtils;
import com.agents.builder.main.domain.vo.SystemSettingVo;
import com.agents.builder.main.service.ISystemSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationInit implements ApplicationRunner {

    private final ISystemSettingService systemSettingService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initEmailSetting();
    }

    private void initEmailSetting() {
        SystemSettingVo settingVo = systemSettingService.getByType(SettingType.EMAIL);
        if (settingVo!=null) {
            MailAccount mailAccount = systemSettingService.buildEmailAccount(settingVo.getMeta());
            MailUtils.setMailAccount(mailAccount);
            log.info("============初始化邮件配置==============");
            return;
        }
    }
}
