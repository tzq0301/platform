package nju.ics.platformclientspringbootstarter.handler;

import jakarta.annotation.Nonnull;

import java.lang.reflect.Method;

public record HandlerInstance(@Nonnull Object obj, @Nonnull Method method) {
}
