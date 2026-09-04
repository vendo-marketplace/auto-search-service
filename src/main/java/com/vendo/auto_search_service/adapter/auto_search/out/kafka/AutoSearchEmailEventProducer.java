package com.vendo.auto_search_service.adapter.auto_search.out.kafka;

import com.vendo.event_lib.auto_search.AutoSearchEmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoSearchEmailEventProducer {

    @Value("${kafka.events.notification.auto-search-email-event.topic}")
    private String topic;

    private final KafkaTemplate<String, AutoSearchEmailEvent> kafkaTemplate;

    public void send(AutoSearchEmailEvent event) {
        kafkaTemplate.send(topic, event);
        log.info("Sent event for auto search email: {}.", event);
    }

}
