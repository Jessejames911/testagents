package com.agents.builder.main.service;

import com.agents.builder.common.core.domain.model.PasswordLoginBody;
import com.agents.builder.common.core.enums.EmailType;
import com.agents.builder.main.domain.vo.UserTargetVo;
import com.agents.builder.main.domain.vo.UserVo;
import com.agents.builder.main.domain.bo.UserBo;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.mybatis.core.page.PageQuery;

import java.util.List;

/**
 * Service接口
 *
 * @author Angus
 * @date 2024-10-31
 */
public interface IUserService {

    /**
     * 查询
     */
    UserVo queryById(Long id);

    /**
     * 查询列表
     */
    TableDataInfo<UserVo> queryPageList(UserBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<UserVo> queryList(UserBo bo);

    /**
     * 新增
     */
    Boolean insertByBo(UserBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(UserBo bo);

    /**
     * 校验并批量删除信息
     */
    Boolean deleteWithValidById(Long id, Boolean isValid);

    String login(PasswordLoginBody loginBody);

    void checkUserAllowed(Long userId);

    Boolean resetUserPwd(Long userId, String password);

    void checkUserDataScope(Long userId);

    List<UserTargetVo> getUserTargetList(String targetType);

    Boolean sendCodeEmail(EmailType emailType);
}
