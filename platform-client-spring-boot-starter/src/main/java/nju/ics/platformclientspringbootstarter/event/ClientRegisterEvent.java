package nju.ics.platformclientspringbootstarter.event;

import org.springframework.context.ApplicationEvent;

public class ClientRegisterEvent extends ApplicationEvent {
    public ClientRegisterEvent() {
        super(new Object());
    }
}
