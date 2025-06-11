package io.github.lklbjn.passkey.passkeydemo.model.request;

import lombok.Data;

/**
 * @author lklbjn
 * @DATE 2025/6/10 16:44
 */
@Data
public class PasskeyRegistrationRequest {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 凭证
     */
    private String credential;

    /**
     * 备注
     */
    private String describe;
}
