package com.vendo.auto_search_service.infrastructure.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "auto-search.matching")
public class MatchingProps {

    private int pageSize;

}
