package nju.ics.platformserver.server.controller.interceptor;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Type;

@RestControllerAdvice
@Slf4j
public class LogInterceptor extends RequestBodyAdviceAdapter implements ResponseBodyAdvice<Object> {
    @Resource
    HttpServletRequest request;

    @Override
    public boolean supports(@Nonnull MethodParameter methodParameter, @Nonnull Type targetType, @Nonnull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Nonnull
    @Override
    public Object afterBodyRead(@Nonnull Object body, @Nonnull HttpInputMessage inputMessage, @Nonnull MethodParameter parameter, @Nonnull Type targetType, @Nonnull Class<? extends HttpMessageConverter<?>> converterType) {
        String message = String.format("REQUEST: method = [%s], path = [%s], body = [%s]", request.getMethod(), request.getRequestURI(), body);
        log.info(message);

        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }

    // -------------------------------------------------------------------------------
    // Request   ⬆️
    // -------------------------------------------------------------------------------
    // Response  ⬇️
    // -------------------------------------------------------------------------------

    @Override
    public boolean supports(@Nonnull MethodParameter returnType, @Nonnull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, @Nonnull MethodParameter returnType, @Nonnull MediaType selectedContentType, @Nonnull Class<? extends HttpMessageConverter<?>> selectedConverterType, @Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response) {
        String message = String.format("RESPONSE: method = [%s], path = [%s], body = [%s]", request.getMethod(), request.getURI().getPath(), body);
        log.info(message);

        return body;
    }
}
