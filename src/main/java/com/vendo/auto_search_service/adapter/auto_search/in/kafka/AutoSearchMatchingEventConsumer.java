package com.vendo.auto_search_service.adapter.auto_search.in.kafka;

import com.vendo.auto_search_service.AutoSearchMatchingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class AutoSearchMatchingEventConsumer {

    @KafkaListener(
            topics = "${kafka.events.auto-search.matching.topic}",
            groupId = "${kafka.events.auto-search.matching.groupId}",
            properties = {"auto.offset.reset: ${kafka.events.auto-search.matching.properties.auto-offset-reset}"},
            containerFactory = "${kafka.events.auto-search.matching.container-factory}"
    )
    void listenAutoSearchMatchingEvent(AutoSearchMatchingEvent event) {
        log.info("Received event for auto search matching: {}.", event);
        // TODO search for products
    }
}
