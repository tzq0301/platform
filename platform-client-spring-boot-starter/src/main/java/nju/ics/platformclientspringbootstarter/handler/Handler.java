package nju.ics.platformclientspringbootstarter.handler;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Handler {
    String topic();
}
