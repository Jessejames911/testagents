package com.agents.builder.main.controller;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.lang.Assert;
import com.agents.builder.common.core.constant.CacheConstants;
import com.agents.builder.common.core.constant.PermissionConstants;
import com.agents.builder.common.core.constant.UserConstants;
import com.agents.builder.common.core.domain.model.LoginUser;
import com.agents.builder.common.core.domain.model.PasswordLoginBody;
import com.agents.builder.common.core.enums.EmailType;
import com.agents.builder.common.core.enums.UserType;
import com.agents.builder.common.redis.utils.RedissonUtils;
import com.agents.builder.common.satoken.utils.LoginHelper;
import com.agents.builder.main.domain.vo.UserTargetVo;
import com.agents.builder.main.service.impl.LoginService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.agents.builder.common.web.annotation.RepeatSubmit;
import com.agents.builder.common.web.annotation.Log;
import com.agents.builder.common.web.core.BaseController;
import com.agents.builder.common.mybatis.core.page.PageQuery;
import com.agents.builder.common.core.domain.R;
import com.agents.builder.common.core.validate.AddGroup;
import com.agents.builder.common.web.enums.BusinessType;
import com.agents.builder.main.domain.vo.UserVo;
import com.agents.builder.main.domain.bo.UserBo;
import com.agents.builder.main.service.IUserService;
import com.agents.builder.common.mybatis.core.page.TableDataInfo;

/**
 *
 *
 * @author Angus
 * @date 2024-10-31
 */
@Validated
@RequiredArgsConstructor
@RestController
public class UserController extends BaseController {

    private final IUserService userService;

    private final LoginService loginService;

    @PostMapping("/user/login")
    @SaIgnore
    public R<String> login(@RequestBody @Validated PasswordLoginBody loginBody) {
        return R.ok("登录成功",userService.login(loginBody));
    }

    @PostMapping("/user/logout")
    public R<String> logout() {
        loginService.logout();
        return R.ok("退出成功");
    }

    /**
     * 获取详细信息
     *
     * @param id 主键
     */

    @GetMapping("/user")
    public R<LoginUser> getLoginUser() {
        return R.ok(LoginHelper.getLoginUser());
    }

    /**
     * 获取详细信息
     *
     * @param id 主键
     */

    @GetMapping("/user/{id}")
    public R<UserVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(userService.queryById(id));
    }

    @GetMapping("/user/list")
    @SaCheckPermission(PermissionConstants.USER_READ)
    public R<List<UserVo>> getAllList(UserBo bo) {
        return R.ok(userService.queryList(bo));
    }

    @GetMapping("/user/list/{targetType}")
    @SaCheckPermission(PermissionConstants.USER_READ)
    public R<List<UserTargetVo>> getUserTargetList(@PathVariable String targetType) {
        return R.ok(userService.getUserTargetList(targetType));
    }



    @PostMapping("/user/current/send_email")
    @SaCheckPermission(PermissionConstants.USER_READ)
    @RepeatSubmit(interval = 60, timeUnit = TimeUnit.SECONDS,message = "1分钟内已发送过邮件请勿重复发送")
    public R<?> sendEmail() {
        return R.ok(userService.sendCodeEmail(EmailType.RESET_PASSWORD));
    }

    @PostMapping("/user/current/reset_password")
    @SaCheckPermission(PermissionConstants.USER_READ)
    @RepeatSubmit()
    public R<?> resetPassword(@RequestBody UserBo user) {
        user.setId(LoginHelper.getUserId());
        Assert.notEmpty(user.getPassword());
        Assert.notEmpty(user.getRePassword());
        Assert.equals(user.getPassword(),user.getRePassword());
        Assert.notNull(user.getCode());
        Integer cacheCode = RedissonUtils.getCacheObject(CacheConstants.CODE_EMAIL_PREFIX + EmailType.RESET_PASSWORD.getType() + ":" + user.getId());
        Assert.notNull(cacheCode,"验证码已过期");
        Assert.equals(cacheCode,user.getCode(),"验证码有误");
        userService.checkUserAllowed(user.getId());
        userService.checkUserDataScope(user.getId());
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        return toAjax(userService.resetUserPwd(user.getId(), user.getPassword()));
    }

    /**
     * 查询列表
     */

    @GetMapping("/user_manage/{pageNum}/{pageSize}")
    @SaCheckPermission(PermissionConstants.USER_READ)
    public TableDataInfo<UserVo> list(@PathVariable Integer pageNum,@PathVariable Integer pageSize, UserBo bo, PageQuery pageQuery) {
        pageQuery.setPageNum(pageNum);
        pageQuery.setPageSize(pageSize);
        return userService.queryPageList(bo, pageQuery);
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/user_manage")
    @SaCheckPermission(PermissionConstants.USER_CREATE)
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UserBo bo) {
        bo.setPassword(BCrypt.hashpw(bo.getPassword()));
        bo.setRole(UserConstants.COMMON_USER);
        bo.setIsActive(true);
        bo.setSource(UserType.SYS_USER.getUserType());
        return toAjax(userService.insertByBo(bo));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/user_manage/{id}")
    @SaCheckPermission(PermissionConstants.USER_UPDATE)
    public R<Void> edit(@PathVariable Long id,@RequestBody UserBo bo) {
        bo.setId(id);
        return toAjax(userService.updateByBo(bo));
    }

    /**
     * 删除
     *
     * @param ids 主键串
     */

    @Log(title = "", businessType = BusinessType.DELETE)
    @DeleteMapping("/user_manage/{id}")
    @SaCheckPermission(PermissionConstants.USER_DELETE)
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long id) {
        return toAjax(userService.deleteWithValidById(id, true));
    }


    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/re_password")
    @SaCheckPermission(PermissionConstants.USER_UPDATE)
    public R<Void> resetPwd(@PathVariable Long id,@RequestBody UserBo user) {
        Assert.notEmpty(user.getPassword());
        Assert.notEmpty(user.getRePassword());
        user.setId(id);
        userService.checkUserAllowed(user.getId());
        userService.checkUserDataScope(user.getId());
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        return toAjax(userService.resetUserPwd(user.getId(), user.getPassword()));
    }
}
