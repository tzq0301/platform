package nju.ics.platformserver.server.controller.interceptor;

import jakarta.annotation.Nonnull;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

@RestControllerAdvice
public class XFrameInterceptor implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@Nonnull MethodParameter returnType, @Nonnull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, @Nonnull MethodParameter returnType, @Nonnull MediaType selectedContentType, @Nonnull Class<? extends HttpMessageConverter<?>> selectedConverterType, @Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response) {
        response.getHeaders().addAll("X-Frame-Options", List.of("deny", "sameorgin"));
        return body;
    }
}
