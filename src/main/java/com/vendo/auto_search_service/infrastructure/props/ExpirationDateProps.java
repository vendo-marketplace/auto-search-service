package com.vendo.auto_search_service.infrastructure.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "auto-search.expiration")
public class ExpirationDateProps {

    private int minDays;
    private int maxDays;

}
