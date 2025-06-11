package io.github.lklbjn.passkey.passkeydemo.model.request;

import io.github.lklbjn.passkey.passkeydemo.model.entity.User;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户主表
 */
@Data
public class UserAddRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 唯一用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;


    public User toUser() {
        User user = new User();
        user.setUsername(this.username);
        user.setEmail(this.email);

        // Not mapped User fields:
        // id
        // userHandle
        // createdAt
        // updatedAt
        return user;
    }
}