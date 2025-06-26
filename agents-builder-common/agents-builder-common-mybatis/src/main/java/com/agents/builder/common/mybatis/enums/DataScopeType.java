package com.agents.builder.common.mybatis.enums;

import com.agents.builder.common.mybatis.helper.DataPermissionHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.agents.builder.common.core.utils.StringUtils;

/**
 * 数据权限类型
 * <p>
 * 语法支持 spel 模板表达式
 * <p>
 * 内置数据 user 当前用户 内容参考 LoginUser
 * 如需扩展数据 可使用 {@link DataPermissionHelper} 操作
 * 内置服务 sdss 系统数据权限服务 内容参考 SysDataScopeService
 * 如需扩展更多自定义服务 可以参考 sdss 自行编写
 *
 *
 * @version 3.5.0
 */
@Getter
@AllArgsConstructor
public enum DataScopeType {

    /**
     * 全部数据权限
     */
    ALL("ADMIN", "", ""),

    /**
     * 自定数据权限
     */
    CUSTOM("USER", " #{#target} IN ( #{@sdss.getUserCustom( #user.userId,#targetType,#isSelect )} ) ", " 1 = 0 "),

    /**
     * 仅本人数据权限
     */
    SELF("SIMPLE_USER", " #{#createUser} = #{#user.userId} ", " 1 = 0 ");

    private final String code;

    /**
     * 语法 采用 spel 模板表达式
     */
    private final String sqlTemplate;

    /**
     * 不满足 sqlTemplate 则填充
     */
    private final String elseSql;

    public static DataScopeType findCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (DataScopeType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
