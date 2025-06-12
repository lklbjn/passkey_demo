package io.github.lklbjn.passkey.passkeydemo.service;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import io.github.lklbjn.passkey.passkeydemo.model.entity.WebauthnCredential;
import io.github.lklbjn.passkey.passkeydemo.model.request.PasskeyRegistrationRequest;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author lklbjn
 */
@Service
public class PasskeyAuthorizationService {

    @Resource
    private UserService userService;
    @Resource
    private RelyingParty relyingParty;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private WebauthnCredentialService webauthnCredentialRepository;
    private static final String REDIS_PASSKEY_REGISTRATION_KEY = "passkey:registration";
    private static final String REDIS_PASSKEY_ASSERTION_KEY = "passkey:assertion";

    /**
     * 开始 Passkey 注册流程
     *
     * @param userId
     * @return java.lang.String
     * @author lklbjn
     * @version 1.0.0.0
     * @since 10:36 2025/6/6
     */
    public String startPasskeyRegistration(Long userId) {
        // 获取用户信息，创建注册选项，将选项存储在 Redis 中，返回给客户端用于创建凭证的 JSON
        var user = userService.getUserByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        var options = relyingParty.startRegistration(StartRegistrationOptions.builder()
                .user(UserIdentity.builder()
                        .name(user.getEmail())
                        .displayName(user.getUsername())
                        .id(user.getUserHandle())
                        .build())
                .authenticatorSelection(AuthenticatorSelectionCriteria.builder()
                        .residentKey(ResidentKeyRequirement.REQUIRED)
                        .build())
                .build());
        try {
            stringRedisTemplate.opsForHash().put(REDIS_PASSKEY_REGISTRATION_KEY, String.valueOf(user.getId()), options.toJson());
            return options.toCredentialsCreateJson();
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    /**
     * 完成 Passkey 注册流程
     *
     * @param passkey
     * @return void
     * @author lklbjn
     * @version 1.0.0.0
     * @since 10:36 2025/6/6
     */
    public void finishPasskeyRegistration(PasskeyRegistrationRequest passkey) throws IOException, RegistrationFailedException {
        // 解析客户端返回的凭证，从 Redis 获取之前的请求，验证并完成注册，最后存储凭证
        var user = userService.getUserByUserId(passkey.getUserId());
        var pkc = PublicKeyCredential.parseRegistrationResponseJson(passkey.getCredential());

        var request = PublicKeyCredentialCreationOptions.fromJson((String)
                stringRedisTemplate.opsForHash().get(REDIS_PASSKEY_REGISTRATION_KEY, String.valueOf(user.getId())));

        var result = relyingParty.finishRegistration(FinishRegistrationOptions.builder()
                .request(request)
                .response(pkc)
                .build());
        stringRedisTemplate.opsForHash().delete(REDIS_PASSKEY_REGISTRATION_KEY, String.valueOf(user.getId()));

        storeCredential(user.getId(), passkey.getDescribe(), request, result);
    }

    /**
     * 开始 Passkey 断言（登录）流程
     *
     * @param identifier
     * @return java.lang.String
     * @author lklbjn
     * @version 1.0.0.0
     * @since 10:37 2025/6/6
     */
    public String startPasskeyAssertion(String identifier) throws JsonProcessingException {
        // 创建断言选项，将选项存储在 Redis 中，返回给客户端用于获取凭证的 JSON
        var options = relyingParty.startAssertion(StartAssertionOptions.builder().build());

        stringRedisTemplate.opsForHash().put(REDIS_PASSKEY_ASSERTION_KEY, identifier, options.toJson());

        return options.toCredentialsGetJson();
    }

    /**
     * 完成 Passkey 断言（登录）流程
     *
     * @param identifier
     * @param credential
     * @return io.github.lklbjn.passkey.passkeydemo.entity.User
     * @author lklbjn
     * @version 1.0.0.0
     * @since 10:37 2025/6/6
     */
    public WebauthnCredential finishPasskeyAssertion(String identifier, String credential) throws IOException, AssertionFailedException {
        // 解析客户端返回的凭证，从 Redis 获取之前的请求，验证断言，更新凭证，返回用户 ID
        var request = AssertionRequest.fromJson((String)
                stringRedisTemplate.opsForHash().get(REDIS_PASSKEY_ASSERTION_KEY, identifier));
        var pkc = PublicKeyCredential.parseAssertionResponseJson(credential);

        var result = relyingParty.finishAssertion(FinishAssertionOptions.builder()
                .request(request)
                .response(pkc)
                .build());

        stringRedisTemplate.opsForHash().delete(REDIS_PASSKEY_ASSERTION_KEY, identifier);

        if (!result.isSuccess()) {
            throw new AssertionFailedException("Verify failed");
        }

        // 从结果中获取用户ID
        return updateCredential(result.getCredential().getCredentialId(), result);
    }

    /**
     * 存储用户的凭证信息到数据库
     *
     * @param id
     * @param describe
     * @param request
     * @param result
     * @return void
     * @author lklbjn
     * @version 1.0.0.0
     * @since 15:35 2025/6/11
     */
    private void storeCredential(Long id,
                                 @NotNull String describe,
                                 @NotNull PublicKeyCredentialCreationOptions request,
                                 @NotNull RegistrationResult result) {
        webauthnCredentialRepository.save(fromFinishPasskeyRegistration(id, describe, request, result));
    }

    /**
     * 更新用户的凭证信息（主要是签名计数）
     *
     * @param credentialId
     * @param result
     */
    private WebauthnCredential updateCredential(@NotNull ByteArray credentialId,
                                                @NotNull AssertionResult result) {
        var entity = webauthnCredentialRepository.getByCredentialId(credentialId);
        entity.setSignatureCount(result.getSignatureCount());
        webauthnCredentialRepository.saveOrUpdate(entity);
        return entity;
    }

    /**
     * 从注册结果创建 WebauthnCredential 对象
     *
     * @param id
     * @param describe
     * @param request
     * @param result
     * @return
     */
    @NotNull
    private static WebauthnCredential fromFinishPasskeyRegistration(Long id,
                                                                    String describe,
                                                                    PublicKeyCredentialCreationOptions request,
                                                                    RegistrationResult result) {
        UserIdentity user = request.getUser();
        TreeSet<String> transports = result.getKeyId().getTransports().orElseGet(TreeSet::new)
                .stream().map(AuthenticatorTransport::getId).collect(Collectors.toCollection(TreeSet::new));
        String requestJson = null;
        try {
            requestJson = request.toJson();
        } catch (JsonProcessingException e) {
            requestJson = "";
        }
        String resultJson = JSONObject.toJSONString(RegisteredCredential.builder()
                .credentialId(result.getKeyId().getId())
                .userHandle(request.getUser().getId())
                .publicKeyCose(result.getPublicKeyCose())
                .signatureCount(result.getSignatureCount())
                .build());
        return WebauthnCredential.builder().userId(id)
                .username(user.getName()).displayName(user.getDisplayName()).credentialId(result.getKeyId().getId())
                .publicKeyCose(result.getPublicKeyCose())
                .signatureCount(result.getSignatureCount())
                .transportsJson(JSONObject.toJSONString(transports))
                .registrationTime(LocalDateTime.now())
                .backupEligible(result.isBackupEligible())
                .backupState(result.isBackedUp())
                .credentialRegistrationRequest(requestJson)
                .credentialRegistrationResult(resultJson)
                .describe(describe)
                .build();
    }

}