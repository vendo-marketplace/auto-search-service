package com.vendo.auto_search_service.adapter.search.out.config;

import com.vendo.auto_search_service.adapter.search.out.exception.SearchServiceErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchFeignConfig {

    @Bean
    public ErrorDecoder searchErrorDecoder() {
        return new SearchServiceErrorDecoder();
    }

}
