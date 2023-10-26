package nju.ics.platformclientspringbootstarter.controller;

import nju.ics.platformclientspringbootstarter.event.ClientRegisterEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/platform/client/status")
public class StatusController {
    private volatile boolean ready;

    @GetMapping("/ready")
    public boolean ready() {
        return ready;
    }

    @EventListener(ClientRegisterEvent.class)
    private void changeStatuesToReady() {
        ready = true;
    }
}
