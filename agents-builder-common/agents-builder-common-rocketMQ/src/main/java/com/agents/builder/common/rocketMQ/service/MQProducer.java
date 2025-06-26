package com.agents.builder.common.rocketMQ.service;

import com.alibaba.fastjson.JSON;
import com.agents.builder.common.rocketMQ.annotation.SecureInvoke;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * Description: 发送mq工具类
 * Author: Angus
 * Date: 2023-08-12
 */
@Slf4j
public class MQProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void sendMsg(String topic, Object body) {
        log.info("===>发送消息：topic={}, body={}", topic, JSON.toJSONString(body));
        Message<Object> build = MessageBuilder.withPayload(body).build();
        rocketMQTemplate.send(topic, build);
    }

    /**
     * 发送可靠消息，在事务提交后保证发送成功
     *
     * @param topic
     * @param body
     */
    @SecureInvoke
    public void sendSecureMsg(String topic, Object body, Object key,Boolean orderly) {
        log.info("===>发送事务消息：topic={}, body={} key={}", topic, JSON.toJSONString(body),key);
        Message<Object> build = MessageBuilder
                .withPayload(body)
                .setHeader("KEYS", key)
                .build();

        if (orderly){
            rocketMQTemplate.syncSendOrderly(topic, build, topic);
        }else {
            rocketMQTemplate.send(topic, build);
        }
    }
}
