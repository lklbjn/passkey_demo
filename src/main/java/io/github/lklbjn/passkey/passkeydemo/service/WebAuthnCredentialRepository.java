package io.github.lklbjn.passkey.passkeydemo.service;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import io.github.lklbjn.passkey.passkeydemo.model.entity.User;
import io.github.lklbjn.passkey.passkeydemo.model.entity.WebauthnCredential;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lklbjn
 */
@Service
public class WebAuthnCredentialRepository implements CredentialRepository {

    @Resource
    private UserService userService;
    @Resource
    private WebauthnCredentialService webauthnCredentialService;

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        return webauthnCredentialService.findAllByUserId(userService.getUserIdByEmail(username)).stream()
                .map(it -> PublicKeyCredentialDescriptor.builder()
                        .id(it.getCredentialId())
                        .transports(it.getTransports())
                        .build())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        return Optional.ofNullable(userService.getUserHandleByEmail(username));
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        return getRegistrationsByUserHandle(userHandle).stream()
                .findAny().map(WebauthnCredential::getUsername);
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        Optional<WebauthnCredential> registrationMaybe = webauthnCredentialService
                .lambdaQuery().eq(WebauthnCredential::getCredentialId, credentialId).list().stream().findAny();
        User user = userService.getUserByUserId(registrationMaybe.map(WebauthnCredential::getUserId).orElse(null));
        if (user == null) {
            return Optional.empty();
        }
        if (!user.getUserHandle().equals(userHandle)) {
            return Optional.empty();
        }
        return registrationMaybe.map(it ->
                RegisteredCredential.builder()
                        .credentialId(it.getCredentialId())
                        .userHandle(userHandle)
                        .publicKeyCose(it.getPublicKeyCose())
                        .signatureCount(it.getSignatureCount())
                        .build());
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        List<WebauthnCredential> credentials = webauthnCredentialService.lambdaQuery()
                .eq(WebauthnCredential::getCredentialId, credentialId).list();
        if (credentials.isEmpty()) {
            return Set.of();
        }
        User user = userService.getUserByUserId(credentials.get(0).getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return credentials.stream().map(it ->
                        RegisteredCredential.builder()
                                .credentialId(it.getCredentialId())
                                .userHandle(user.getUserHandle())
                                .publicKeyCose(it.getPublicKeyCose())
                                .signatureCount(it.getSignatureCount())
                                .build())
                .collect(Collectors.toUnmodifiableSet());
    }

    private Collection<WebauthnCredential> getRegistrationsByUserHandle(ByteArray userHandle) {
        var userId = userService.getUserIdByUserHandle(userHandle);
        return webauthnCredentialService.lambdaQuery()
                .eq(WebauthnCredential::getUserId, userId).list();
    }
}