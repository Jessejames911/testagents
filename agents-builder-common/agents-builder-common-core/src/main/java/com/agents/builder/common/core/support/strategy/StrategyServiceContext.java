package com.agents.builder.common.core.support.strategy;

import cn.hutool.core.lang.Assert;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 策略接口中心
 *
 * @author Angus
 * @date 2024/06/26
 */
public abstract class StrategyServiceContext<Service> {

    protected List<Service> serviceList;

    public StrategyServiceContext(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    protected abstract Function<Service, String> serviceKey();

    protected abstract Function<Service, Set<String>> bindingServiceKey();

    /**
     * 根据key获取对应策略器
     *
     * @param key               指定唯一标识
     * @param exceptionSupplier 自定义异常信息
     * @return
     */
    public Service getService(String key, Supplier<RuntimeException> exceptionSupplier) {
        for (Service service : serviceList) {
            String serviceTypeKey = this.serviceKey().apply(service);
            Assert.notEmpty(serviceTypeKey, exceptionSupplier);
            if (serviceTypeKey.toUpperCase(Locale.ROOT).equals(key.toUpperCase(Locale.ROOT))) {
                return service;
            }
        }
        if (Objects.nonNull(exceptionSupplier)){
            throw exceptionSupplier.get();
        }
        return null;
    }


    public Service getBindingService(String key, Supplier<RuntimeException> exceptionSupplier) {
        for (Service service : serviceList) {
            Set<String> serviceTypeKey = this.bindingServiceKey().apply(service);
            Assert.notEmpty(serviceTypeKey, exceptionSupplier);
            if (serviceTypeKey.contains(key)) {
                return service;
            }
        }
        if (Objects.nonNull(exceptionSupplier)){
            throw exceptionSupplier.get();
        }
        return null;
    }



}
