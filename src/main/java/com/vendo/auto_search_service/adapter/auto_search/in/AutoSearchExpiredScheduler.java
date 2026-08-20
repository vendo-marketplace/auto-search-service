package com.vendo.auto_search_service.adapter.auto_search.in;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AutoSearchExpiredScheduler {

    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
    public void processStatusExpired() {

    }

}

