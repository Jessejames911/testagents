package com.agents.builder.main.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import com.agents.builder.common.core.enums.SettingType;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.file.FileUtils;
import com.agents.builder.common.mail.utils.MailAccount;
import com.agents.builder.common.mail.utils.MailUtils;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.oss.core.OssClient;
import com.agents.builder.common.oss.entity.UploadResult;
import com.agents.builder.main.domain.SystemSetting;
import com.agents.builder.main.domain.bo.SystemSettingBo;
import com.agents.builder.main.domain.dto.DisplaySettingsDto;
import com.agents.builder.main.domain.vo.SystemSettingVo;
import com.agents.builder.main.mapper.SystemSettingMapper;
import com.agents.builder.main.service.ISystemSettingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
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
@Slf4j
public class SystemSettingServiceImpl implements ISystemSettingService {

    private final SystemSettingMapper baseMapper;

    private final OssClient ossClient;

    /**
     * 查询
     */
    @Override
    public SystemSettingVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<SystemSettingVo> queryPageList(SystemSettingBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SystemSetting> lqw = buildQueryWrapper(bo);
        Page<SystemSettingVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<SystemSettingVo> queryList(SystemSettingBo bo) {
        LambdaQueryWrapper<SystemSetting> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SystemSetting> buildQueryWrapper(SystemSettingBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SystemSetting> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getType() != null, SystemSetting::getType, bo.getType());
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(SystemSettingBo bo) {
        SystemSetting add = MapstructUtils.convert(bo, SystemSetting.class);
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
    public Boolean updateByBo(SystemSettingBo bo) {
        SystemSetting update = MapstructUtils.convert(bo, SystemSetting.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SystemSetting entity) {
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
    public SystemSettingVo getByType(SettingType settingType) {
        return baseMapper.selectVoOne(new LambdaQueryWrapper<SystemSetting>()
                .eq(SystemSetting::getType, settingType.getCode()));
    }

    @Override
    public Boolean saveOrUpdateEmailSetting(Map<String, Object> emailSetting) {
        return saveOrUpdate(SettingType.EMAIL, emailSetting);
    }

    private Boolean saveOrUpdate(SettingType settingType, Map<String, Object> settingMap) {
        SystemSettingVo settingVo = getByType(settingType);
        SystemSettingBo settingBo = new SystemSettingBo();
        settingBo.setMeta(settingMap);
        if (settingVo != null) {
            settingBo.setId(settingVo.getId());
            return updateByBo(settingBo);
        }
        settingBo.setType(settingType.getCode());
        return insertByBo(settingBo);
    }


    @Override
    @SneakyThrows
    public Boolean saveOrUpdateDisplaySetting(DisplaySettingsDto dto) {
        Map<String, Object> map = BeanUtil.beanToMap(dto);

        String basePath = SettingType.DISPLAY.getName() + "/";
        if (dto.getLoginLogo() != null && !(dto.getLoginLogo() instanceof String)) {
            MultipartFile loginLogo = (MultipartFile) dto.getLoginLogo();
            UploadResult loginLogoRes = ossClient.upload(loginLogo.getInputStream(), basePath + FileUtils.getMd5(loginLogo.getInputStream()), MediaType.IMAGE_JPEG_VALUE);
            map.put("loginLogo", loginLogoRes.getUrl());
        }

        if (dto.getIcon() != null && !(dto.getIcon() instanceof String)) {
            MultipartFile icon = (MultipartFile) dto.getIcon();
            UploadResult iconRes = ossClient.upload(icon.getInputStream(), basePath + FileUtils.getMd5(icon.getInputStream()), MediaType.IMAGE_JPEG_VALUE);
            map.put("icon", iconRes.getUrl());
        }

        if (dto.getLoginImage() != null && !(dto.getLoginImage() instanceof String)) {
            MultipartFile loginImage = (MultipartFile) dto.getLoginImage();
            UploadResult loginImageRes = ossClient.upload(loginImage.getInputStream(), basePath + FileUtils.getMd5(loginImage.getInputStream()), MediaType.IMAGE_JPEG_VALUE);
            map.put("loginImage", loginImageRes.getUrl());
        }

        return saveOrUpdate(SettingType.DISPLAY, map);
    }

    @Override
    public Boolean emailTest(Map<String, Object> emailSetting) {
        MailAccount mailAccount = buildEmailAccount(emailSetting);
        try {
            MailUtils.send(mailAccount, mailAccount.getUser(), "测试邮件", "这是一封测试邮件，无需回复。", false);
        } catch (Exception e) {
            log.error("测试邮件发送失败", e);
            throw new ServiceException("测试失败" + e.getMessage());
        }
        return true;
    }

    public MailAccount buildEmailAccount(Map<String, Object> emailSetting) {
        MailAccount account = new MailAccount();
        account.setHost((String) emailSetting.get("email_host"));
        account.setPort(Integer.parseInt((String) emailSetting.get("email_port")));
        account.setAuth(true);
        account.setFrom("Agents Builder Plant Form" + "<" + emailSetting.get("from_email") + ">");
        account.setUser((String) emailSetting.get("from_email"));
        account.setPass((String) emailSetting.get("email_host_password"));
        account.setStarttlsEnable((Boolean) emailSetting.get("email_use_tls"));
        account.setSslEnable((Boolean) emailSetting.get("email_use_ssl"));
        return account;
    }


}
