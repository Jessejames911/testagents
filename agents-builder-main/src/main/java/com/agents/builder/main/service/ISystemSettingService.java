package com.agents.builder.main.service;

import com.agents.builder.common.core.enums.SettingType;
import com.agents.builder.common.mail.utils.MailAccount;
import com.agents.builder.main.domain.dto.DisplaySettingsDto;
import com.agents.builder.main.domain.vo.SystemSettingVo;
import com.agents.builder.main.domain.bo.SystemSettingBo;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Service接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface ISystemSettingService {

    /**
     * 查询
     */
    SystemSettingVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<SystemSettingVo> queryPageList(SystemSettingBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<SystemSettingVo> queryList(SystemSettingBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(SystemSettingBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(SystemSettingBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    SystemSettingVo getByType(SettingType settingType);

    Boolean saveOrUpdateEmailSetting(Map<String, Object> emailSetting);

    Boolean emailTest(Map<String, Object> emailSetting);

    MailAccount buildEmailAccount(Map<String, Object> emailSetting);

    Boolean saveOrUpdateDisplaySetting(DisplaySettingsDto dto);
}
