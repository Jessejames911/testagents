package com.agents.builder.main.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.redis.utils.RedisUtils;
import com.agents.builder.common.redis.utils.RedissonUtils;
import com.agents.builder.main.constants.ChatConstants;
import com.agents.builder.main.domain.ApplicationChatRecord;
import com.agents.builder.main.domain.bo.ApplicationChatRecordBo;
import com.agents.builder.main.domain.bo.ParagraphBo;
import com.agents.builder.main.domain.bo.ProblemBo;
import com.agents.builder.main.domain.dto.DocImproveDto;
import com.agents.builder.main.domain.vo.ApplicationChatRecordVo;
import com.agents.builder.main.domain.vo.DatasetVo;
import com.agents.builder.main.domain.vo.ParagraphVo;
import com.agents.builder.main.mapper.ApplicationChatRecordMapper;
import com.agents.builder.main.mapper.DatasetMapper;
import com.agents.builder.main.service.IApplicationChatRecordService;
import com.agents.builder.main.service.IParagraphService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
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
public class ApplicationChatRecordServiceImpl implements IApplicationChatRecordService {

    private final ApplicationChatRecordMapper baseMapper;

    private final IParagraphService paragraphService;

    private final DatasetMapper datesetMapper;

    /**
     * 查询
     */
    @Override
    public ApplicationChatRecordVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    @Override
    public ApplicationChatRecordVo getDetail(Long id, Long chatId) {
        ApplicationChatRecordVo chatRecordVo = baseMapper.selectVoById(id);
        if (chatRecordVo==null){
            String info = (String) RedisUtils.hGet(ChatConstants.TEMP_CHAT_RECORD_CACHE_KEY, chatId.toString());
            List<ApplicationChatRecord> chatRecords = JSON.parseArray(info, ApplicationChatRecord.class);
            if (chatRecords!=null){
                chatRecordVo = MapstructUtils.convert(chatRecords.stream().filter(item->item.getId().equals(id)).findFirst().orElse(new ApplicationChatRecord()),ApplicationChatRecordVo.class);
            }
        }
        List<ParagraphVo> paragraphVoList = paragraphService.selectVoBatchIds(chatRecordVo.getImproveParagraphIdList());
        chatRecordVo.setParagraphList(paragraphVoList);
        if (CollUtil.isNotEmpty(paragraphVoList)) {
            chatRecordVo.setDatasetList(datesetMapper.selectVoBatchIds(paragraphVoList.stream().map(ParagraphVo::getDatasetId).collect(Collectors.toList())));
        }
        return chatRecordVo;
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<ApplicationChatRecordVo> queryPageList(ApplicationChatRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ApplicationChatRecord> lqw = buildQueryWrapper(bo);
        Page<ApplicationChatRecordVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);

        List<Long> paragraphIdList = result.getRecords().stream().flatMap(item -> item.getImproveParagraphIdList().stream()).distinct().collect(Collectors.toList());

        if (CollUtil.isNotEmpty(paragraphIdList)){

            HashMap<Long, DatasetVo> paragraphDatesetMap = new HashMap<>();

            List<ParagraphVo> paragraphVoList = paragraphService.selectVoBatchIds(paragraphIdList);

            Map<Long, DatasetVo> datasetVoMap = datesetMapper.selectVoBatchIds(paragraphVoList.stream().map(ParagraphVo::getDatasetId).collect(Collectors.toList()))
                    .stream().distinct().collect(Collectors.toMap(DatasetVo::getId, item -> item));

            paragraphVoList.forEach(item -> paragraphDatesetMap.put(item.getId(), datasetVoMap.get(item.getDatasetId())));

            Map<Long, ParagraphVo> paragraphVoMap = paragraphVoList.stream().collect(Collectors.toMap(ParagraphVo::getId, item -> item));

            // 组装关联的知识库与分段数据
            result.getRecords().forEach(item -> {
                List<ParagraphVo> improveParagraphList = item.getImproveParagraphIdList().stream().map(paragraphVoMap::get).collect(Collectors.toList());
                List<DatasetVo> datasetList = CollUtil.distinct(improveParagraphList.stream().map(p->paragraphDatesetMap.get(p.getId())).filter(Objects::nonNull).collect(Collectors.toList()),DatasetVo::getId,false);
                item.setDatasetList(datasetList);
                item.setParagraphList(improveParagraphList);
            });
        }

        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<ApplicationChatRecordVo> queryList(ApplicationChatRecordBo bo) {
        LambdaQueryWrapper<ApplicationChatRecord> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ApplicationChatRecord> buildQueryWrapper(ApplicationChatRecordBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ApplicationChatRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getVoteStatus()!=null, ApplicationChatRecord::getVoteStatus, bo.getVoteStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getProblemText()), ApplicationChatRecord::getProblemText, bo.getProblemText());
        lqw.eq(StringUtils.isNotBlank(bo.getAnswerText()), ApplicationChatRecord::getAnswerText, bo.getAnswerText());
        lqw.eq(bo.getMessageTokens() != null, ApplicationChatRecord::getMessageTokens, bo.getMessageTokens());
        lqw.eq(bo.getAnswerTokens() != null, ApplicationChatRecord::getAnswerTokens, bo.getAnswerTokens());
        lqw.eq(bo.getRunTime() != null, ApplicationChatRecord::getRunTime, bo.getRunTime());
        lqw.eq(bo.getChatId() != null, ApplicationChatRecord::getChatId, bo.getChatId());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(ApplicationChatRecordBo bo) {
        ApplicationChatRecord add = MapstructUtils.convert(bo, ApplicationChatRecord.class);
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
    public Boolean updateByBo(ApplicationChatRecordBo bo) {
        ApplicationChatRecord update = MapstructUtils.convert(bo, ApplicationChatRecord.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ApplicationChatRecord entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    @Transactional
    public Boolean deleteByChatId(Collection<Long> chatIds) {
        List<ApplicationChatRecord> chatRecordList = getByChatId(chatIds);
        if (CollUtil.isEmpty(chatRecordList)){
            return true;
        }
        return deleteWithValidByIds(chatRecordList.stream().map(ApplicationChatRecord::getId).collect(Collectors.toList()), true);
    }

    @Override
    public Boolean insert(ApplicationChatRecord chatRecords) {
        return baseMapper.insert(chatRecords) > 0;
    }

    @Override
    public List<ApplicationChatRecord> getLastByChatId(Long chatId, int lastN) {
        return baseMapper.selectList(new LambdaQueryWrapper<ApplicationChatRecord>()
                .eq(ApplicationChatRecord::getChatId,chatId)
                .orderByDesc(ApplicationChatRecord::getCreateTime)
                .last("limit " + lastN));
    }

    @Override
    @Transactional
    public ApplicationChatRecordVo improveDoc(DocImproveDto dto) {
        long paragraphId = IdWorker.getId();

        // 添加段落与问题
        ParagraphBo paragraphBo = new ParagraphBo();
        paragraphBo.setId(paragraphId);
        paragraphBo.setDocumentId(dto.getDocumentId());
        paragraphBo.setContent(dto.getContent());
        paragraphBo.setTitle(dto.getTitle());
        paragraphBo.setDatasetId(dto.getDatasetId());
        paragraphBo.setProblemList(List.of(ProblemBo.builder().content(dto.getProblemText()).build()));
        paragraphService.insertByBo(paragraphBo);

        // 更新分段关联
        ApplicationChatRecord chatRecord = new ApplicationChatRecord();
        chatRecord.setId(dto.getRecordId());
        chatRecord.setImproveParagraphIdList(List.of(paragraphId));
        baseMapper.updateById(chatRecord);

        return queryById(dto.getRecordId());
    }



    private List<ApplicationChatRecord> getByChatId(Collection<Long> chatIds) {
        if (CollUtil.isEmpty(chatIds)){
            return Collections.emptyList();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<ApplicationChatRecord>()
                .in(ApplicationChatRecord::getChatId, chatIds));
    }
}
