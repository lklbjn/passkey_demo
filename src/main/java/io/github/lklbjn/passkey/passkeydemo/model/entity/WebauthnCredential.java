package io.github.lklbjn.passkey.passkeydemo.model.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.ByteArray;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 认证表
 */
@Data
@Builder
@TableName(value = "webauthn_credential")
public class WebauthnCredential implements Serializable {
    public static final String COL_CREDENTIAL_REGISTRATION = "credential_registration";
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 展示名称
     */
    @TableField(value = "display_name")
    private String displayName;

    /**
     * 凭证信息
     */
    @TableField(value = "credential_id")
    private ByteArray credentialId;

    /**
     * 公钥
     */
    @TableField(value = "public_key_cose")
    private ByteArray publicKeyCose;

    /**
     * 防重放攻击计数
     */
    @TableField(value = "signature_count")
    private Long signatureCount;

    /**
     * 传输方式
     */
    @TableField(value = "transports_json")
    private String transportsJson;

    /**
     * 注册时间
     */
    @TableField(value = "registration_time")
    private LocalDateTime registrationTime;

    /**
     * 凭证是否可以备份
     */
    @TableField(value = "backup_eligible")
    private Boolean backupEligible;

    /**
     * 是否已备份
     */
    @TableField(value = "backup_state")
    private Boolean backupState;

    /**
     * 认证
     */
    @TableField(value = "credential_registration_request")
    private String credentialRegistrationRequest;

    @TableField(value = "credential_registration_result")
    private String credentialRegistrationResult;

    @TableField(value = "`describe`")
    private String describe;

    public static final String COL_ID = "id";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_USERNAME = "username";

    public static final String COL_DISPLAY_NAME = "display_name";

    public static final String COL_CREDENTIAL_ID = "credential_id";

    public static final String COL_USER_HANDLE = "user_handle";

    public static final String COL_PUBLIC_KEY_COSE = "public_key_cose";

    public static final String COL_SIGNATURE_COUNT = "signature_count";

    public static final String COL_TRANSPORTS = "transports";

    public static final String COL_REGISTRATION_TIME = "registration_time";

    public static final String COL_BACKUP_ELIGIBLE = "backup_eligible";

    public static final String COL_BACKUP_STATE = "backup_state";

    public static final String CREDENTIAL_REGISTRATION_REQUEST = "credential_registration_request";

    public static final String CREDENTIAL_REGISTRATION_RESULT = "credential_registration_result";

    public static final String DESCRIBE = "describe";

    public SortedSet<AuthenticatorTransport> getTransports() {
        return JSONObject.parseObject(this.transportsJson, new TypeReference<TreeSet<String>>() {
                })
                .stream().map(AuthenticatorTransport::of)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));
    }
}