package com.vendo.auto_search_service.adapter.auto_search.out.kafka;

import com.vendo.auto_search_service.AutoSearchMatchingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoSearchMatchingEventProducer {

    @Value("${kafka.events.auto-search.matching.topic}")
    private String autoSearchMatchingEventTopic;

    private final KafkaTemplate<String, AutoSearchMatchingEvent> kafkaTemplate;

    public void send(AutoSearchMatchingEvent event) {
        kafkaTemplate.send(autoSearchMatchingEventTopic, event);
        log.info("Sent event for product updated: {}.", event);
    }

}
