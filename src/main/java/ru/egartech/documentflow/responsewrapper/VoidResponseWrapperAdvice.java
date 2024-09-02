package ru.egartech.documentflow.responsewrapper;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

@RestControllerAdvice
public class VoidResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getContainingClass().isAnnotationPresent(WrappedResponse.class)
                && returnType.getParameterType().equals(void.class);
    }

    public final Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType contentType,
                                        Class<? extends HttpMessageConverter<?>> converterType,
                                        ServerHttpRequest request, ServerHttpResponse response) {
        return ResponseWrapper.<Void>builder()
                .success(true)
                .status(204)
                .errors(List.of())
                .build();
    }

}