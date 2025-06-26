package com.agents.builder.common.rocketMQ.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 * Author: Angus
 * Date: 2023-08-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecureInvokeDTO {
    private String className;
    private String methodName;
    private String parameterTypes;
    private String args;
}
