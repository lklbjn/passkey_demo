package io.github.lklbjn.passkey.passkeydemo.service;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yubico.webauthn.data.ByteArray;
import io.github.lklbjn.passkey.passkeydemo.event.RefreshUserHandleEvent;
import io.github.lklbjn.passkey.passkeydemo.mapper.UserMapper;
import io.github.lklbjn.passkey.passkeydemo.model.entity.User;
import io.github.lklbjn.passkey.passkeydemo.model.request.UserAddRequest;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author lklbjn
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public Long getUserIdByEmail(String email) {
        return lambdaQuery()
                .eq(User::getEmail, email).list()
                .stream().map(User::getId).findFirst().orElse(null);
    }

    public ByteArray getUserHandleByEmail(String email) {
        return lambdaQuery()
                .eq(User::getEmail, email).list()
                .stream().map(User::getUserHandle).findFirst().orElse(null);
    }

    public User getUserByUserId(Long userId) {
        return lambdaQuery().eq(User::getId, userId).one();
    }

    public void addUser(UserAddRequest request) {
        User user = request.toUser();
        checkEmailHasExisted(user.getEmail());
        user.setUserHandle(generateUserHandle());
        user.setCreatedAt(new Date());
        save(user);
    }

    private void checkEmailHasExisted(String email) {
        if (lambdaQuery().eq(User::getEmail, email).exists()) {
            throw new IllegalArgumentException("Email has existed");
        }
    }

    public ByteArray generateUserHandle() {
        return new ByteArray(RandomUtil.randomBytes(32));
    }

    public Long getUserIdByUserHandle(ByteArray userHandle) {
        return lambdaQuery().eq(User::getUserHandle, userHandle).list()
                .stream().map(User::getId).findFirst().orElse(null);
    }

    public void refreshUserHandle(Long userId) {
        User user = checkUserIsExistAndReturn(userId);
        user.setUserHandle(generateUserHandle());
        updateById(user);
        RefreshUserHandleEvent event = new RefreshUserHandleEvent(this, userId);
        applicationEventPublisher.publishEvent(event);
    }

    private User checkUserIsExistAndReturn(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }
}
