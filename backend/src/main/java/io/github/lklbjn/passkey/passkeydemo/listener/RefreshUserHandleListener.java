package io.github.lklbjn.passkey.passkeydemo.listener;

import io.github.lklbjn.passkey.passkeydemo.event.RefreshUserHandleEvent;
import io.github.lklbjn.passkey.passkeydemo.model.entity.WebauthnCredential;
import io.github.lklbjn.passkey.passkeydemo.service.WebauthnCredentialService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RefreshUserHandleListener implements ApplicationListener<RefreshUserHandleEvent> {

    @Resource
    private WebauthnCredentialService webauthnCredentialService;

    @Override
    public void onApplicationEvent(RefreshUserHandleEvent event) {
        webauthnCredentialService.lambdaUpdate()
                .eq(WebauthnCredential::getUserId, event.getUserId())
                .remove();
        log.info("Refresh user handle for userId: {}", event.getUserId());
    }
}