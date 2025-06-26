package com.agents.builder.main.service;

import com.agents.builder.common.core.workflow.LogicFlow;
import com.agents.builder.main.domain.dto.EmbedDto;
import com.agents.builder.main.domain.dto.ChatMessageDto;
import com.agents.builder.main.domain.dto.SearchDto;
import com.agents.builder.main.domain.vo.ApplicationVo;
import com.agents.builder.main.domain.bo.ApplicationBo;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.main.domain.vo.ChatMessageVo;
import com.agents.builder.main.domain.vo.ParagraphVo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;

/**
 * Service接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface IApplicationService {

    /**
     * 查询
     */
    ApplicationVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<ApplicationVo> queryPageList(ApplicationBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<ApplicationVo> queryList(ApplicationBo bo);

    /**
     * 新增
     */
    ApplicationVo insertByBo(ApplicationBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(ApplicationBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    void embed(EmbedDto dto, HttpServletResponse response);

    List<ParagraphVo> hitTest(SearchDto dto);

    String auth(String accessToken);

    ApplicationVo getProfile();

    Flux<ChatMessageVo> streamChat(ChatMessageDto dto);

    ChatMessageVo chat(ChatMessageDto dto);

    List<ApplicationVo> getVoByIds(List<Long> appIds);

    Boolean publish(Long id, LogicFlow workFlow);

    Boolean deleteByUser(Long userId);

    List<ApplicationVo> getAvailableAppList(Long appId);

    Boolean editIcon(Long id, MultipartFile file);
}
