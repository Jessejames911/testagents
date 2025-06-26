package com.agents.builder.common.core.enums;

import com.agents.builder.common.core.constant.PermissionConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum PermissionOperate {

    APPLICATION_CREATE(PermissionConstants.APPLICATION_CREATE),

    APPLICATION_UPDATE(PermissionConstants.APPLICATION_UPDATE),

    APPLICATION_DELETE(PermissionConstants.APPLICATION_DELETE),

    APPLICATION_READ(PermissionConstants.APPLICATION_READ),

    DATASET_CREATE(PermissionConstants.DATASET_CREATE),

    DATASET_UPDATE(PermissionConstants.DATASET_UPDATE),

    DATASET_DELETE(PermissionConstants.DATASET_DELETE),

    DATASET_READ(PermissionConstants.DATASET_READ),

    TEAM_CREATE(PermissionConstants.TEAM_CREATE),

    TEAM_UPDATE(PermissionConstants.TEAM_UPDATE),

    TEAM_DELETE(PermissionConstants.TEAM_DELETE),

    TEAM_READ(PermissionConstants.TEAM_READ),

    USER_CREATE(PermissionConstants.USER_CREATE),

    USER_UPDATE(PermissionConstants.USER_UPDATE),

    USER_DELETE(PermissionConstants.USER_DELETE),

    USER_READ(PermissionConstants.USER_READ),

    MODEL_CREATE(PermissionConstants.MODEL_CREATE),

    MODEL_UPDATE(PermissionConstants.MODEL_UPDATE),

    MODEL_DELETE(PermissionConstants.MODEL_DELETE),

    MODEL_READ(PermissionConstants.MODEL_READ),

    SETTING_CREATE(PermissionConstants.SETTING_CREATE),

    SETTING_UPDATE(PermissionConstants.SETTING_UPDATE),

    SETTING_DELETE(PermissionConstants.DATASET_DELETE),

    SETTING_READ(PermissionConstants.SETTING_READ),

    FUNCTION_LIB_CREATE(PermissionConstants.FUNCTION_LIB_CREATE),

    FUNCTION_LIB_UPDATE(PermissionConstants.FUNCTION_LIB_UPDATE),

    FUNCTION_LIB_DELETE(PermissionConstants.FUNCTION_LIB_DELETE),

    FUNCTION_LIB_READ(PermissionConstants.FUNCTION_LIB_READ),

    ;

    private final String key;

    public static PermissionOperate getByKey(String key) {
        for (PermissionOperate permissionOperate : PermissionOperate.values()) {
            if (permissionOperate.getKey().equals(key)) {
                return permissionOperate;
            }
        }
        return null;
    }

    public static Set<String> getAllKeys() {
        return Arrays.stream(PermissionOperate.values()).map(PermissionOperate::getKey).collect(Collectors.toSet());
    }

    public static Set<String> getCommonUserOptions() {
        return Arrays.stream(PermissionOperate.values()).filter(permissionOperate -> !permissionOperate.getKey().startsWith("USER") && !(permissionOperate.getKey().startsWith("SETTING")&&!permissionOperate.getKey().contains("READ"))).map(PermissionOperate::getKey).collect(Collectors.toSet());
    }
}
