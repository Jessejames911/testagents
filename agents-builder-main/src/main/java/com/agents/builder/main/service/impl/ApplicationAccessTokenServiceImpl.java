package com.agents.builder.main.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.agents.builder.common.core.exception.ChatLimitException;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.core.utils.file.FileUtils;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.oss.core.OssClient;
import com.agents.builder.common.oss.entity.UploadResult;
import com.agents.builder.common.redis.utils.RedisUtils;
import com.agents.builder.common.redis.utils.RedissonUtils;
import com.agents.builder.main.domain.ApplicationAccessToken;
import com.agents.builder.main.domain.bo.ApplicationAccessTokenBo;
import com.agents.builder.main.domain.vo.ApplicationAccessTokenVo;
import com.agents.builder.main.mapper.ApplicationAccessTokenMapper;
import com.agents.builder.main.service.IApplicationAccessTokenService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Service业务层处理
 *
 * @author Angus
 * @date 2024-10-31
 */
@RequiredArgsConstructor
@Service
public class ApplicationAccessTokenServiceImpl implements IApplicationAccessTokenService {

    private final ApplicationAccessTokenMapper baseMapper;

    private final OssClient ossClient;

    /**
     * 查询
     */
    @Override
    public ApplicationAccessTokenVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<ApplicationAccessTokenVo> queryPageList(ApplicationAccessTokenBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ApplicationAccessToken> lqw = buildQueryWrapper(bo);
        Page<ApplicationAccessTokenVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<ApplicationAccessTokenVo> queryList(ApplicationAccessTokenBo bo) {
        LambdaQueryWrapper<ApplicationAccessToken> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ApplicationAccessToken> buildQueryWrapper(ApplicationAccessTokenBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ApplicationAccessToken> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getApplicationId() != null, ApplicationAccessToken::getApplicationId, bo.getApplicationId());
        lqw.eq(StringUtils.isNotBlank(bo.getAccessToken()), ApplicationAccessToken::getAccessToken, bo.getAccessToken());
        lqw.eq(bo.getIsActive() != null, ApplicationAccessToken::getIsActive, bo.getIsActive());
        lqw.eq(bo.getAccessNum() != null, ApplicationAccessToken::getAccessNum, bo.getAccessNum());
        lqw.eq(bo.getWhiteActive() != null, ApplicationAccessToken::getWhiteActive, bo.getWhiteActive());
        lqw.eq(bo.getShowSource() != null, ApplicationAccessToken::getShowSource, bo.getShowSource());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(ApplicationAccessTokenBo bo) {
        ApplicationAccessToken add = MapstructUtils.convert(bo, ApplicationAccessToken.class);
        validEntityBeforeSave(add);
        return baseMapper.insert(add) > 0;
    }

    /**
     * 修改
     */
    @Override
    @SneakyThrows
    public ApplicationAccessTokenVo updateByBo(ApplicationAccessTokenBo bo) {
        String baseUrl = "embed/";
        if (bo.getAvatar()!=null && !(bo.getAvatar() instanceof String)){
            MultipartFile avatar = (MultipartFile) bo.getAvatar();
            UploadResult result = ossClient.upload(avatar.getInputStream(), baseUrl+ FileUtils.getMd5(avatar.getInputStream()), avatar.getContentType());
            bo.setAvatarUrl(result.getUrl());
        }

        if (bo.getFloat_icon()!=null  && !(bo.getFloat_icon() instanceof String)){
            MultipartFile icon = (MultipartFile) bo.getFloat_icon();
            UploadResult result = ossClient.upload(icon.getInputStream(), baseUrl+ FileUtils.getMd5(icon.getInputStream()), icon.getContentType());
            bo.setFloatIconUrl(result.getUrl());
        }

        if (bo.getUser_avatar()!=null  && !(bo.getUser_avatar() instanceof String)){
            MultipartFile userAvatar = (MultipartFile) bo.getUser_avatar();
            UploadResult result = ossClient.upload(userAvatar.getInputStream(), baseUrl+ FileUtils.getMd5(userAvatar.getInputStream()), userAvatar.getContentType());
            bo.setUserAvatarUrl(result.getUrl());
        }

        ApplicationAccessToken update = MapstructUtils.convert(bo, ApplicationAccessToken.class);
        if (StrUtil.isNotBlank(bo.getCustom_theme())) {
            update.setCustomTheme(JSONObject.parseObject(bo.getCustom_theme(),ApplicationAccessToken.CustomTheme.class));
        }
        update.setDisclaimerValue(bo.getDisclaimer_value());
        if (StrUtil.isNotBlank(bo.getFloat_location())) {
            update.setFloatLocation(JSONObject.parseObject(bo.getFloat_location(),ApplicationAccessToken.FloatLocation.class));
        }
        update.setShowHistory(bo.getShow_history());
        update.setShowGuide(bo.getShow_guide());
        update.setShowSource(bo.getShowSource());
        validEntityBeforeSave(update);
        baseMapper.updateById(update);
        return queryById(update.getApplicationId());
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ApplicationAccessToken entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public ApplicationAccessToken getByToken(String accessToken) {
        return baseMapper.selectOne(new LambdaQueryWrapper<ApplicationAccessToken>()
                .eq(ApplicationAccessToken::getAccessToken, accessToken));
    }

    @Override
    public Boolean insertAppDefault(Long appId) {
        ApplicationAccessToken accessToken = new ApplicationAccessToken();
        accessToken.setApplicationId(appId);
        accessToken.setAccessToken(UUID.fastUUID().toString(true));
        return baseMapper.insert(accessToken) > 0;
    }

    @Override
    public Boolean deleteByAppId(Collection<Long> appIds) {
        if (CollUtil.isEmpty(appIds)) {
            return true;
        }
        return baseMapper.delete(new LambdaQueryWrapper<ApplicationAccessToken>()
                .in(ApplicationAccessToken::getApplicationId, appIds)) > 0;
    }

    @Override
    public void checkAccessNum(Long clientId, Long appId) {
        ApplicationAccessToken accessToken = baseMapper.selectById(appId);
        String key = "access_num:" + appId + ":" + clientId;
        Long accessNum = accessToken.getAccessNum();

        boolean exists = RedisUtils.hasKey(key);

        if (exists) {
            long atomicValue = RedisUtils.incr(key,1);
            if (atomicValue >= accessNum) {
                throw new ChatLimitException(461, "对话次数超过限制");
            }
        }else {
            DateTime dateTime = DateUtil.beginOfDay(DateUtil.offsetDay(new Date(), 1));
            RedisUtils.set(key, 0,dateTime.getTime() - System.currentTimeMillis()-1000);
        }
    }
}
