package io.github.lklbjn.passkey.passkeydemo.config;

import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import io.github.lklbjn.passkey.passkeydemo.service.WebAuthnCredentialRepository;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;

/**
 * @author lklbjn
 * @DATE 2025/6/5 13:42
 */
@Slf4j
@Configuration
@ComponentScan("io.github.lklbjn.passkey.passkeydemo")
@MapperScan("io.github.lklbjn.passkey.passkeydemo.mapper")
public class PasskeyConfig {

    // 核心服务类
    @Bean
    public RelyingParty relyingParty(WebAuthnCredentialRepository credentialRepo) {
        return RelyingParty.builder()
                .identity(RelyingPartyIdentity.builder()
                        // .id("passkey.ciallo.io")
                        .id("localhost")
                        .name("PassKeyDemo")
                        .build())
                .credentialRepository(credentialRepo)
                .origins(new HashSet<>(List.of("http://localhost:8080","http://localhost:8193"))) // 允许的前端源
                .build();
    }

}
