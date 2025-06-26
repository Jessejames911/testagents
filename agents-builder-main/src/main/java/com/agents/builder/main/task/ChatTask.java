package com.agents.builder.main.task;

import com.agents.builder.main.domain.Application;
import com.agents.builder.main.mapper.ApplicationMapper;
import com.agents.builder.main.service.IApplicationChatService;
import com.agents.builder.main.service.IApplicationService;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatTask {

    private final IApplicationChatService applicationChatService;

    private final ApplicationMapper applicationMapper;

    private final LockTemplate lockTemplate;


    @Scheduled(cron = "0 0 1 * * ? ")
    @Async
    public void cleanExpiredChatSessions() {
        final LockInfo lockInfo = lockTemplate.lock("cleanExpiredChatSessions", 30000L, 0L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            log.error("===========任务已在其他节点执行========");
            return;
        }
        try {
            log.info("=========清除过期的会话===============");
            List<Application> applicationList = applicationMapper.selectList();
            for (Application application : applicationList) {
                applicationChatService.cleanExpiredChat(application.getId(),application.getCleanTime());
            }
        } finally {
            lockTemplate.releaseLock(lockInfo);
        }
    }
}
