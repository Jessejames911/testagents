package com.agents.builder.main.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.agents.builder.common.core.config.properties.SystemConfigResourceProperties;
import com.agents.builder.common.core.constant.CacheConstants;
import com.agents.builder.common.core.constant.Constants;
import com.agents.builder.common.core.domain.model.LoginUser;
import com.agents.builder.common.core.domain.model.PasswordLoginBody;
import com.agents.builder.common.core.enums.EmailType;
import com.agents.builder.common.core.enums.LoginType;
import com.agents.builder.common.core.exception.ServiceException;
import com.agents.builder.common.core.service.UserService;
import com.agents.builder.common.core.utils.MapstructUtils;
import com.agents.builder.common.core.utils.MessageUtils;
import com.agents.builder.common.core.utils.StringUtils;
import com.agents.builder.common.mail.utils.MailUtils;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;
import com.agents.builder.common.redis.utils.RedissonUtils;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.agents.builder.main.domain.User;
import com.agents.builder.main.domain.bo.UserBo;
import com.agents.builder.main.domain.vo.UserTargetVo;
import com.agents.builder.main.domain.vo.UserVo;
import com.agents.builder.main.mapper.UserMapper;
import com.agents.builder.main.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Service业务层处理
 *
 * @author Angus
 * @date 2024-10-31
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService, UserService {

    private final UserMapper baseMapper;

    private final LoginService loginService;

    private final IApplicationService applicationService;

    private final IDatasetService datasetService;

    private final IFunctionLibService functionLibService;

    private final IModelService modelService;

    private final ITeamService teamService;

    private final ITeamMemberService teamMemberService;

    private final SystemConfigResourceProperties systemConfigResourceProperties;



    /**
     * 查询
     */
    @Override
    public UserVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询列表
     */
    @Override
    public TableDataInfo<UserVo> queryPageList(UserBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<User> lqw = buildQueryWrapper(bo);
        Page<UserVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询列表
     */
    @Override
    public List<UserVo> queryList(UserBo bo) {
        LambdaQueryWrapper<User> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<User> buildQueryWrapper(UserBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getEmail()), User::getEmail, bo.getEmail());
        lqw.eq(StringUtils.isNotBlank(bo.getPhone()), User::getPhone, bo.getPhone());
        lqw.like(StringUtils.isNotBlank(bo.getNickName()), User::getNickName, bo.getNickName());
        lqw.like(StringUtils.isNotBlank(bo.getUsername()), User::getUsername, bo.getUsername());
        lqw.eq(StringUtils.isNotBlank(bo.getPassword()), User::getPassword, bo.getPassword());
        lqw.eq(StringUtils.isNotBlank(bo.getRole()), User::getRole, bo.getRole());
        lqw.eq(bo.getIsActive() != null, User::getIsActive, bo.getIsActive());
        lqw.eq(StringUtils.isNotBlank(bo.getSource()), User::getSource, bo.getSource());
        lqw.and(StringUtils.isNotBlank(bo.getEmail_or_username()),l->l.like(User::getUsername, bo.getEmail_or_username())
                .or().like(User::getEmail, bo.getEmail_or_username()));
        return lqw;
    }

    /**
     * 新增
     */
    @Override
    public Boolean insertByBo(UserBo bo) {
        User add = MapstructUtils.convert(bo, User.class);
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
    public Boolean updateByBo(UserBo bo) {
        User update = MapstructUtils.convert(bo, User.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(User entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public Boolean deleteWithValidById(Long id, Boolean isValid) {
        if (!LoginHelper.isSuperAdmin())return false;
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        // 删除创建的应用
        applicationService.deleteByUser(id);
        // 删除创建的知识库
        datasetService.deleteByUser(id);
        // 删除创建的模型
        modelService.deleteByUser(id);
        // 删除创建的函数库
        functionLibService.deleteByUser(id);
        // 删除团队
        teamService.deleteByUser(id);

        // 删除自己所在团队
        teamMemberService.deleteByUser(id);
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public String login(PasswordLoginBody loginBody) {
        String username = loginBody.getUsername();
        String password = loginBody.getPassword();

        UserVo user = loadUserByUsername(username);
        if (!user.getIsActive()){
            throw new ServiceException("账号已被禁用");
        }
        loginService.checkLogin(LoginType.PASSWORD, username, () -> !BCrypt.checkpw(password, user.getPassword()));
        return loginService.handleLogin(user,null);
    }





    @Override
    public void checkUserAllowed(Long userId) {
        if (LoginHelper.isSuperAdmin()){
            return;
        }
        if (ObjectUtil.isNotNull(userId) && LoginHelper.isSuperAdmin(userId)) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    @Override
    public Boolean resetUserPwd(Long userId, String password) {
        User user = new User();
        user.setId(userId);
        user.setPassword(password);
        return baseMapper.updateById(user) > 0;
    }

    @Override
    public void checkUserDataScope(Long userId) {
        if (ObjectUtil.isNull(userId)) {
            return;
        }
        if (LoginHelper.isSuperAdmin()) {
            return;
        }
        if (ObjectUtil.isNull(baseMapper.selectById(userId))) {
            throw new ServiceException("没有权限访问用户数据！");
        }
    }

    @Override
    public List<UserTargetVo> getUserTargetList(String targetType) {
        return List.of(UserTargetVo.builder().id("all").username("全部").build(),
                UserTargetVo.builder().id(LoginHelper.getUserId().toString()).username("我的").build()
                );
    }

    @Override
    public Boolean sendCodeEmail(EmailType emailType) {
        Long userId = LoginHelper.getLoginUser().getUserId();
        User user = baseMapper.selectById(userId);
        String codeEmailTemplatePath = systemConfigResourceProperties.getCodeEmailTemplatePath();
        if (StringUtils.isBlank(codeEmailTemplatePath)) {
            throw new ServiceException("请先配置邮件模板路径");
        }
        if (StringUtils.isBlank(user.getEmail())) {
            throw new ServiceException("请先配置邮箱");
        }
        int code = RandomUtil.getRandom().nextInt(100000,1000000);

        String template = FileUtil.readString(codeEmailTemplatePath, StandardCharsets.UTF_8);

        try {
            MailUtils.send(user.getEmail(), emailType.getDesc(), template.replace("${code}", String.valueOf(code)), true);
            RedissonUtils.setCacheObject(CacheConstants.CODE_EMAIL_PREFIX + emailType.getType()+ ":"+ userId, code, Duration.ofMinutes(30));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private UserVo loadUserByUsername(String username) {
        return baseMapper.selectVoOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public String selectUserNameById(Long userId) {
        return queryById(userId).getUsername();
    }
}
