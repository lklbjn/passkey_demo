package io.github.lklbjn.passkey.passkeydemo.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author lklbjn
 */
@Getter
public class RefreshUserHandleEvent extends ApplicationEvent {

    private final Long userId;

    public RefreshUserHandleEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }


}
