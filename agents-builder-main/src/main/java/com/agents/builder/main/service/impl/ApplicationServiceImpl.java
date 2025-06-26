package com.agents.builder.main.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.agents.builder.common.core.config.properties.SystemConfigResourceProperties;
import com.agents.builder.common.core.constant.Constants;
import com.agents.builder.common.core.enums.OperateTargetType;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.exception.StreamException;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.ServletUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.core.utils.file.FileUtils;
import com.agents.builder.common.core.workflow.LfNode;
import com.agents.builder.common.core.workflow.LogicFlow;
import com.agents.builder.common.core.workflow.enums.NodeType;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.oss.core.OssClient;
import com.agents.builder.common.oss.entity.UploadResult;
import com.agents.builder.common.redis.utils.RedisUtils;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.agents.builder.main.constants.ChatConstants;
import com.agents.builder.main.domain.*;
import com.agents.builder.main.domain.bo.ApplicationBo;
import com.agents.builder.main.domain.bo.ApplicationChatBo;
import com.agents.builder.main.domain.bo.ApplicationDatasetMappingBo;
import com.agents.builder.main.domain.dto.ChatMessageDto;
import com.agents.builder.main.domain.dto.EmbedDto;
import com.agents.builder.main.domain.dto.SearchDto;
import com.agents.builder.main.domain.vo.*;
import com.agents.builder.main.enums.AppType;
import com.agents.builder.main.mapper.ApplicationMapper;
import com.agents.builder.main.mapper.UserMapper;
import com.agents.builder.main.service.*;
import com.agents.builder.main.strategy.ChatMessageStrategy;
import com.agents.builder.main.strategy.context.ChatMessageContext;
import com.agents.builder.main.strategy.context.SearchContext;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.File;
import java.nio.charset.StandardCharsets;
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
@Slf4j
public class ApplicationServiceImpl implements IApplicationService {

    private final SystemConfigResourceProperties systemConfigResourceProperties;

    private final ApplicationMapper baseMapper;

    private final IApplicationAccessTokenService accessTokenService;

    private final IApplicationDatasetMappingService applicationDatasetMappingService;

    private final IApplicationApiKeyService apiKeyService;

    private final IApplicationPublicAccessClientService publicAccessClientService;

    private final IApplicationChatService chatService;

    private final LoginService loginService;

    private final IApplicationWorkFlowVersionService workFlowVersionService;

    private final ChatMessageContext chatMessageContext;

    private final SearchContext searchContext;

    private final ITeamMemberPermissionService teamMemberPermissionService;

    private final UserMapper userMapper;

    private final OssClient ossClient;


    /**
     * 查询
     */
    @Override
    public ApplicationVo queryById(Long id) {
        List<Long> datasetIds = applicationDatasetMappingService.queryList(ApplicationDatasetMappingBo.builder().applicationId(id).build()).stream().map(ApplicationDatasetMappingVo::getDatasetId).collect(Collectors.toList());
        ApplicationVo applicationVo = baseMapper.selectVoById(id);
        applicationVo.setDatasetIdList(datasetIds);
        return applicationVo;
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<ApplicationVo> queryPageList(ApplicationBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Application> lqw = buildQueryWrapper(bo);
        Page<ApplicationVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<ApplicationVo> queryList(ApplicationBo bo) {
        LambdaQueryWrapper<Application> lqw = buildQueryWrapper(bo);
        List<ApplicationVo> applicationVos = baseMapper.selectVoList(lqw);
        applicationVos.forEach(item->item.setUserId(item.getCreateBy().toString()));
        return applicationVos;
    }

    private LambdaQueryWrapper<Application> buildQueryWrapper(ApplicationBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Application> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), Application::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), Application::getDescription, bo.getDescription());
        lqw.eq(StringUtils.isNotBlank(bo.getPrologue()), Application::getPrologue, bo.getPrologue());
        lqw.eq(bo.getDialogueNumber() != null, Application::getDialogueNumber, bo.getDialogueNumber());
        lqw.eq(bo.getProblemOptimization() != null, Application::getProblemOptimization, bo.getProblemOptimization());
        lqw.eq(bo.getModelId() != null, Application::getModelId, bo.getModelId());
        lqw.eq(StringUtils.isNotBlank(bo.getIcon()), Application::getIcon, bo.getIcon());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), Application::getType, bo.getType());
        lqw.eq(bo.getSttModelId() != null, Application::getSttModelId, bo.getSttModelId());
        lqw.eq(bo.getSttModelEnable() != null, Application::getSttModelEnable, bo.getSttModelEnable());
        lqw.eq(bo.getTtsModelId() != null, Application::getTtsModelId, bo.getTtsModelId());
        lqw.eq(bo.getTtsModelEnable() != null, Application::getTtsModelEnable, bo.getTtsModelEnable());
        lqw.eq(StringUtils.isNotBlank(bo.getTtsType()), Application::getTtsType, bo.getTtsType());
        lqw.eq(StringUtils.isNotBlank(bo.getProblemOptimizationPrompt()), Application::getProblemOptimizationPrompt, bo.getProblemOptimizationPrompt());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    @Transactional
    public ApplicationVo insertByBo(ApplicationBo bo) {
        Application add = MapstructUtils.convert(bo, Application.class);
        add.setId(IdWorker.getId());
        validEntityBeforeSave(add);

        // 添加应用与数据集映射关系
        if (CollUtil.isNotEmpty(bo.getDatasetIdList())) {
            saveDatasetMapping(bo, add);
        }

        if (AppType.WORKFLOW.getKey().equals(bo.getType())) {
            add.setWorkFlow(getDefaultWorkflow());
        }

        // 添加默认accessToken
        accessTokenService.insertAppDefault(add.getId());

        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return queryById(add.getId());
    }

    private LogicFlow getDefaultWorkflow() {
        // 获取默认工作流
        File file = FileUtil.file(systemConfigResourceProperties.getDefaultWorkflowJsPath());
        if (!file.exists()) {
            log.error("获取默认工作流失败");
            return null;
        }
        return JSON.parseObject(FileUtil.readString(file, StandardCharsets.UTF_8), LogicFlow.class);
    }

    private void saveDatasetMapping(ApplicationBo bo, Application add) {
        List<ApplicationDatasetMapping> datasetMappings = bo.getDatasetIdList().stream().map(item -> {
            ApplicationDatasetMapping datasetMapping = new ApplicationDatasetMapping();
            datasetMapping.setDatasetId(item);
            datasetMapping.setApplicationId(add.getId());
            return datasetMapping;
        }).collect(Collectors.toList());
        applicationDatasetMappingService.saveBatch(datasetMappings);
    }

    /**
     * 修改
     */
    @Override
    @Transactional
    public Boolean updateByBo(ApplicationBo bo) {
        Application update = MapstructUtils.convert(bo, Application.class);
        validEntityBeforeSave(update);
        Application application = baseMapper.selectById(bo.getId());
        if (CollUtil.isNotEmpty(bo.getDatasetIdList())) {
            // 删除旧数据
            applicationDatasetMappingService.deleteByAppId(List.of(update.getId()));
            // 新增
            saveDatasetMapping(bo, update);
        }

        if (AppType.WORKFLOW.getKey().equals(application.getType()) && bo.getWorkFlow()!=null) {
            setBaseInfo(bo.getWorkFlow(), update);
        }

        return baseMapper.updateById(update) > 0;
    }

    private void setBaseInfo(LogicFlow workFlow, Application app) {
        LfNode baseNode = CollUtil.findOne(workFlow.getNodes(), node -> node.getId().equals(NodeType.BASE.getKey()));
        Map<String, Object> nodeData = (Map<String, Object>) baseNode.getProperties().get("node_data");
        if (nodeData != null) {
            app.setName((String) nodeData.get("name"));
            app.setDescription((String) nodeData.get("desc"));
            app.setTtsType((String) nodeData.get("tts_type"));
            app.setPrologue((String) nodeData.get("prologue"));
        }
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Application entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        // 删除accessToken
        accessTokenService.deleteByAppId(ids);
        // 删除api key
        apiKeyService.deleteByAppId(ids);
        // 删除会话
        chatService.deleteByAppId(ids);
        // 删除publicAccessClient
        publicAccessClientService.deleteByAppId(ids);
        // 删除工作流版本
        workFlowVersionService.deleteByAppId(ids);
        // 删除知识库关联
        applicationDatasetMappingService.deleteByAppId(ids);
        // 删除权限
        teamMemberPermissionService.deleteByTargetIds(ids);
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    @SneakyThrows
    public void embed(EmbedDto dto, HttpServletResponse response) {
        File file = FileUtil.file(systemConfigResourceProperties.getEmbedJsPath());
        if (!file.exists()) {
            throw new ServiceException("获取应用嵌入js脚本文件失败");
        }
        ApplicationAccessToken token = accessTokenService.getByToken(dto.getToken());
        if (token == null || !token.getIsActive()) {
            throw new ServiceException("token无效或未被启用");
        }
        Set<String> whiteList = token.getWhiteList();
        if (token.getWhiteActive() && !whiteList.contains(ServletUtils.getClientIP())) {
            throw new ServiceException("非法访问，请联系管理员添加白名单");
        }


        String content = FileUtil.readString(file, StandardCharsets.UTF_8);

        String template = render(content,getParamsMap(token, dto));

        response.setContentType("text/javascript;charset=UTF-8");
        response.getWriter().write(template);
    }

    private Map<String, String> getParamsMap(ApplicationAccessToken token, EmbedDto dto) {
        String floatIcon = dto.getProtocol() + "://" + dto.getHost() + "/ui/favicon.ico";
        Set<String> whiteList = token.getWhiteList();
        Map<String, String> map = new HashMap<>();
        map.put("is_auth", "true");
        map.put("protocol", dto.getProtocol());
        map.put("query", Optional.ofNullable(dto.getQuery()).orElse(""));
        map.put("host", dto.getHost());
        map.put("token", dto.getToken());
        map.put("white_list_str", whiteList == null ? "" : whiteList.stream().collect(Collectors.joining(System.lineSeparator())));
        map.put("white_active", token.getWhiteActive().toString());
        map.put("float_icon", Optional.ofNullable(token.getFloatIconUrl()).orElse(floatIcon));
        map.put("is_draggable", token.getDraggable().toString());
        map.put("show_history", token.getShowHistory().toString());
        map.put("show_guide", token.getShowGuide().toString());
        ApplicationAccessToken.FloatLocation floatLocation = token.getFloatLocation();
        map.put("x_type", floatLocation.getX().getType());
        map.put("y_type", floatLocation.getY().getType());
        map.put("x_value", floatLocation.getX().getValue().toString());
        map.put("y_value", floatLocation.getY().getValue().toString());
        return map;
    }

    private String render(String content, Map<String, String> variables) {
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return content;
    }

    @Override
    public List<ParagraphVo> hitTest(SearchDto dto) {
        List<Long> datasetIdList = applicationDatasetMappingService.getByAppId(dto.getApplication_id()).stream().map(ApplicationDatasetMappingVo::getDatasetId).collect(Collectors.toList());
        dto.setDatasetIdList(datasetIdList);
        return searchContext.getService(dto.getSearch_mode(), () -> new ServiceException("暂不支持此检索模式")).search(dto);
    }

    @Override
    public String auth(String accessToken) {
        ApplicationAccessToken token = accessTokenService.getByToken(accessToken);
        if (token == null) {
            throw new ServiceException("认证失败");
        }
        if (!token.getIsActive()) {
            throw new ServiceException("token被禁用");
        }
        if (token.getWhiteActive() && CollUtil.isNotEmpty(token.getWhiteList())) {
            String clientIP = ServletUtils.getClientIP();
            if (!token.getWhiteList().contains(clientIP)) {
                throw new ServiceException("无权访问，不在白名单范围内");
            }
        }
        Boolean temp = null;
        try {
            temp = (Boolean) StpUtil.getStpLogic().getExtra(Constants.TEMP_TOKEN);
        } catch (Exception e) {
            return loginService.loginApplication(token.getCreateBy(), token);
        }
        String extraToken = (String) StpUtil.getStpLogic().getExtra(Constants.ACCESS_TOKEN);
        if (!accessToken.equals(extraToken)){
            return loginService.loginApplication(token.getCreateBy(), token);
        }
        if (temp != null && temp){
            return StpUtil.getTokenValue();
        }
        return loginService.loginApplication(token.getCreateBy(), token);
    }

    @Override
    public ApplicationVo getProfile() {
        ApplicationVo applicationVo = queryById(LoginHelper.getLoginUser().getAppId());
        applicationVo.setWorkFlow(null);
        ApplicationAccessTokenVo accessTokenVo = accessTokenService.queryById(LoginHelper.getLoginUser().getAppId());
        BeanUtil.copyProperties(accessTokenVo, applicationVo);
        return applicationVo;
    }


    private Object doChat(ChatMessageDto dto, boolean isStream) {
        ApplicationChatVo chatVo = chatService.queryById(dto.getChatId());

        Object appInfo = RedisUtils.get(ChatConstants.TEMP_CHAT_APP_MAPPING_KEY + dto.getChatId().toString());

        if (chatVo == null && appInfo == null) {
            ChatMessageVo messageVo = ChatMessageVo.builder()
                    .content("会话已过期或不存在")
                    .chatId(dto.getChatId())
                    .operate(true)
                    .isEnd(true)
                    .build();
            return isStream ? Flux.just(messageVo) : messageVo;
        }
        boolean isTempChat = chatVo == null && appInfo != null;

        ApplicationVo appVO = getApplicationVO(isTempChat, appInfo, chatVo);
        String contactTemplate = null;
        if (!isTempChat) {

            if (StringUtils.isEmpty(chatVo.getAbstractName())) {
                // 更新会话名称
                ApplicationChatBo bo = new ApplicationChatBo();
                bo.setId(chatVo.getId());
                bo.setAbstractName(dto.getMessage());
                chatService.updateByBo(bo);
            }
            if (chatVo.getClientId() != null) {
                accessTokenService.checkAccessNum(chatVo.getClientId(), appVO.getId());
            }
            User user = userMapper.selectById(appVO.getCreateBy());
            if (isStream) {
                contactTemplate = String.format("如有问题请邮件联系维护人员 %s 邮箱: %s ，邮件主题：%s", user.getNickName(), user.getEmail(), appVO.getName());
            }
        }

        ChatMessageStrategy service = chatMessageContext.getService(appVO.getType(), () -> new StreamException("未知的应用类型"));

        ChatMessage chatMessage = ChatMessage.builder()
                .app(appVO)
                .chatId(dto.getChatId())
                .message(dto.getMessage())
                .reChat(dto.getReChat())
                .recordId(IdWorker.getId())
                .userParams(dto.getFormData())
                .apiParams(dto.getApiParams())
                .isTempChat(isTempChat)
                .startTime(System.currentTimeMillis())
                .contact(contactTemplate)
                .build();

        return isStream?service.streamChat(chatMessage) : service.chat(chatMessage);
    }

    @Override
    public ChatMessageVo chat(ChatMessageDto dto) {
        return (ChatMessageVo) doChat(dto, false);
    }

    @Override
    public Flux<ChatMessageVo> streamChat(ChatMessageDto dto) {
        return (Flux<ChatMessageVo>) doChat(dto, true);
    }


    private ApplicationVo getApplicationVO(boolean isTempChat, Object appInfo, ApplicationChatVo chatVo) {
        if (isTempChat) {
            if (appInfo instanceof String) {
                return queryById(Long.parseLong((String) appInfo));
            } else {
                return ApplicationVo.builder()
                        .workFlow((LogicFlow) appInfo)
                        .type(AppType.WORKFLOW.getKey())
                        .build();
            }
        }
        return queryById(chatVo.getApplicationId());
    }

    @Override
    public List<ApplicationVo> getVoByIds(List<Long> appIds) {
        if (CollUtil.isEmpty(appIds)) {
            return Collections.emptyList();
        }
        return baseMapper.selectVoBatchIds(appIds);
    }

    @Override
    public Boolean publish(Long id, LogicFlow workFlow) {
        ApplicationWorkFlowVersion flowVersion = new ApplicationWorkFlowVersion();
        flowVersion.setWorkFlow(workFlow);
        flowVersion.setApplicationId(id);
        return workFlowVersionService.insert(flowVersion);
    }

    @Override
    public Boolean deleteByUser(Long userId) {
        List<Long> appIdList = getByUser(userId).stream().map(Application::getId).collect(Collectors.toList());
        if (CollUtil.isEmpty(appIdList)){
            return true;
        }
        return deleteWithValidByIds(appIdList, true);
    }

    @Override
    public List<ApplicationVo> getAvailableAppList(Long appId) {
        Application application = baseMapper.selectById(appId);

        List<TeamMemberPermissionVo> userTargetPermissions = teamMemberPermissionService.getUserTargetPermissions(application.getCreateBy(), OperateTargetType.APP);

        if (CollUtil.isEmpty(userTargetPermissions)){
            return baseMapper.selectVoList(new LambdaQueryWrapper<Application>()
                    .eq(Application::getCreateBy,application.getCreateBy())
                    .ne(Application::getId,appId));
        }
        List<Long> appIds = userTargetPermissions.stream().filter(item -> !appId.equals(item.getTarget()) && item.getOperate().get("USE") || item.getOperate().get("MANAGE")).map(TeamMemberPermissionVo::getTarget).collect(Collectors.toList());

        return baseMapper.selectVoBatchIds(appIds);
    }

    @Override
    @SneakyThrows
    public Boolean editIcon(Long id, MultipartFile file) {
        UploadResult result = ossClient.upload(file.getInputStream(), "icon/" + FileUtils.getMd5(file.getInputStream()), file.getContentType());
        return baseMapper.update(new LambdaUpdateWrapper<Application>()
                .set(Application::getIcon, result.getUrl())
                .eq(Application::getId, id)) > 0;
    }

    private List<Application> getByUser(Long userId) {
        return baseMapper.selectList(new LambdaQueryWrapper<Application>()
                .eq(Application::getCreateBy, userId));
    }


    private ApplicationVo getByChatId(Long chatId) {
        ApplicationChatVo chatVo = chatService.queryById(chatId);
        if (chatVo == null) return null;
        return queryById(chatVo.getApplicationId());
    }
}
