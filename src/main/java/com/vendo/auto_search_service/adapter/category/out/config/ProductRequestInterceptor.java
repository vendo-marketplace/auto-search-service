package com.vendo.auto_search_service.adapter.category.out.config;

import com.vendo.auto_search_service.adapter.security.out.jwt.InternalTokenGenerationPort;
import com.vendo.core_lib.type.ServiceName;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.vendo.security_lib.http.HttpUtils.AUTHORIZATION_HEADER;
import static com.vendo.security_lib.http.HttpUtils.BEARER_PREFIX;

@Configuration
@RequiredArgsConstructor
public class ProductRequestInterceptor {

    private final InternalTokenGenerationPort internalTokenGenerationPort;

    @Bean
    RequestInterceptor internalProductInfoInterceptor() {
        return request -> request.header(
                AUTHORIZATION_HEADER,
                BEARER_PREFIX + internalTokenGenerationPort.generate(ServiceName.PRODUCT_SERVICE)
        );
    }

}
