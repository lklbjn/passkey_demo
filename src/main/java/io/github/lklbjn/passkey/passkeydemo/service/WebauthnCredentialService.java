package io.github.lklbjn.passkey.passkeydemo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yubico.webauthn.data.ByteArray;
import io.github.lklbjn.passkey.passkeydemo.model.entity.WebauthnCredential;
import io.github.lklbjn.passkey.passkeydemo.mapper.WebauthnCredentialMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebauthnCredentialService extends ServiceImpl<WebauthnCredentialMapper, WebauthnCredential> {

    // 根据用户 ID 获取该用户的所有凭据信息
    public List<WebauthnCredential> findAllByUserId(Long userId) {
        return lambdaQuery().eq(WebauthnCredential::getUserId, userId).list();
    }

    public WebauthnCredential getByCredentialId(ByteArray credentialId) {
        return lambdaQuery().eq(WebauthnCredential::getCredentialId, credentialId).one();
    }
}
