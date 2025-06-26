package com.agents.builder.main.service;

import com.agents.builder.main.domain.ApplicationChatRecord;
import com.agents.builder.main.domain.dto.DocImproveDto;
import com.agents.builder.main.domain.vo.ApplicationChatRecordVo;
import com.agents.builder.main.domain.bo.ApplicationChatRecordBo;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * Service接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface IApplicationChatRecordService {

    /**
     * 查询
     */
    ApplicationChatRecordVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<ApplicationChatRecordVo> queryPageList(ApplicationChatRecordBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<ApplicationChatRecordVo> queryList(ApplicationChatRecordBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(ApplicationChatRecordBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(ApplicationChatRecordBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    Boolean deleteByChatId(Collection<Long> chatIds);

    Boolean insert(ApplicationChatRecord chatRecords);

    List<ApplicationChatRecord> getLastByChatId(Long chatId, int lastN);

    ApplicationChatRecordVo improveDoc(DocImproveDto dto);

    ApplicationChatRecordVo getDetail(Long id, Long chatId);
}
