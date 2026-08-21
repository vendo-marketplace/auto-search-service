package com.vendo.auto_search_service.infrastructure.http;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.vendo.auto_search_service.adapter.category.out")
public class OpenFeignConfig {
}
