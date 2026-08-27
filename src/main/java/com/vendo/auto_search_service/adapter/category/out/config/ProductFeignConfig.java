package com.vendo.auto_search_service.adapter.category.out.config;

import com.vendo.auto_search_service.adapter.category.out.exception.ProductServiceErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductFeignConfig {

    @Bean
    public ErrorDecoder productErrorDecoder() {
        return new ProductServiceErrorDecoder();
    }

}
