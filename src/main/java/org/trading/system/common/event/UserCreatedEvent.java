package org.trading.system.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserCreatedEvent extends ApplicationEvent {

    private final String userId;

    public UserCreatedEvent(Object source, String userId){
        super(source);
        this.userId = userId;
    }
}