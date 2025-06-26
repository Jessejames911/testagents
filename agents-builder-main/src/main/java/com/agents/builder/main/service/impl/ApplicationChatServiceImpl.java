package com.agents.builder.main.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.core.workflow.LogicFlow;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.redis.utils.RedisUtils;
import com.agents.builder.common.redis.utils.RedissonUtils;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.agents.builder.main.constants.ChatConstants;
import com.agents.builder.main.domain.ApplicationChat;
import com.agents.builder.main.domain.bo.ApplicationBo;
import com.agents.builder.main.domain.bo.ApplicationChatBo;
import com.agents.builder.main.domain.vo.ApplicationChatVo;
import com.agents.builder.main.mapper.ApplicationChatMapper;
import com.agents.builder.main.service.IApplicationChatRecordService;
import com.agents.builder.main.service.IApplicationChatService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service业务层处理
 *
 * @author Angus
 * @date 2024-10-31
 */
@RequiredArgsConstructor
@Service
public class ApplicationChatServiceImpl implements IApplicationChatService {

    private final ApplicationChatMapper baseMapper;

    private final IApplicationChatRecordService chatRecordService;

    /**
     * 查询
     */
    @Override
    public ApplicationChatVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<ApplicationChatVo> queryPageList(ApplicationChatBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ApplicationChat> lqw = buildQueryWrapper(bo);
        Page<ApplicationChatVo> result = baseMapper.getVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<ApplicationChatVo> queryList(ApplicationChatBo bo) {
        LambdaQueryWrapper<ApplicationChat> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ApplicationChat> buildQueryWrapper(ApplicationChatBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ApplicationChat> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getAbstractName()), ApplicationChat::getAbstractName, bo.getAbstractName());
        lqw.eq(bo.getApplicationId() != null, ApplicationChat::getApplicationId, bo.getApplicationId());
        lqw.eq(bo.getClientId() != null, ApplicationChat::getClientId, bo.getClientId());
        lqw.between(bo.getStart_time()!=null&&bo.getEnd_time()!=null,ApplicationChat::getCreateTime,bo.getStart_time(),bo.getEnd_time());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(ApplicationChatBo bo) {
        ApplicationChat add = MapstructUtils.convert(bo, ApplicationChat.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改
     */
    @Override
    public Boolean updateByBo(ApplicationChatBo bo) {
        ApplicationChat update = MapstructUtils.convert(bo, ApplicationChat.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ApplicationChat entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        if (CollUtil.isEmpty(ids))return true;
        // 删除对话日志
        chatRecordService.deleteByChatId(ids);
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public String tempOpen(ApplicationBo bo) {
        return tempOpen(bo.getId());
    }

    @Override
    public String tempOpen(Long appId) {
        String chatId = String.valueOf(IdWorker.getId());
        RedisUtils.hSet(ChatConstants.TEMP_CHAT_RECORD_CACHE_KEY,chatId, JSON.toJSONString(new ArrayList<>()), ChatConstants.TEMP_CHAT_RECORD_EXPIRED_TIME);
        RedisUtils.set(ChatConstants.TEMP_CHAT_APP_MAPPING_KEY+chatId,appId.toString(),ChatConstants.TEMP_CHAT_RECORD_EXPIRED_TIME);
        return chatId;
    }


    @Override
    public String tempWorkflowOpen(LogicFlow workFlow) {
        String chatId = String.valueOf(IdWorker.getId());
        RedisUtils.hSet(ChatConstants.TEMP_CHAT_RECORD_CACHE_KEY,chatId,JSON.toJSONString(new ArrayList<>()), ChatConstants.TEMP_CHAT_RECORD_EXPIRED_TIME);
        RedisUtils.set(ChatConstants.TEMP_CHAT_APP_MAPPING_KEY+chatId,workFlow,ChatConstants.TEMP_CHAT_RECORD_EXPIRED_TIME);
        return chatId;
    }

    @Override
    public void cleanExpiredChat(Long appId,Integer saveDay) {
        List<Long> chatIds = baseMapper.selectList(new LambdaQueryWrapper<ApplicationChat>()
                        .lt(ApplicationChat::getCreateTime, DateUtil.offsetDay(new Date(), -saveDay))
                        .eq(ApplicationChat::getApplicationId, appId))
                .stream().map(ApplicationChat::getId).collect(Collectors.toList());
        deleteWithValidByIds(chatIds, true);
    }



    @Override
    public String clientOpen(Long appId) {
        ApplicationChat applicationChat = new ApplicationChat();
        applicationChat.setApplicationId(appId);
        applicationChat.setClientId(LoginHelper.getLoginUser().getClientId());
        baseMapper.insert(applicationChat);
        return applicationChat.getId().toString();
    }

    @Override
    public TableDataInfo<ApplicationChatVo> getVoPage(ApplicationChatBo bo, PageQuery pageQuery) {
        return queryPageList(bo, pageQuery);
    }

    @Override
    @Transactional
    public Boolean deleteByAppId(Collection<Long> appIds) {
        List<ApplicationChat> chatList = getByAppIds(appIds);
        if (CollUtil.isEmpty(chatList)){
            return true;
        }
        return deleteWithValidByIds(chatList.stream().map(ApplicationChat::getId).collect(Collectors.toList()), true);
    }




    private List<ApplicationChat> getByAppIds(Collection<Long> appIds) {
        return baseMapper.selectList(new LambdaQueryWrapper<ApplicationChat>()
                .in(ApplicationChat::getApplicationId, appIds));
    }
}
