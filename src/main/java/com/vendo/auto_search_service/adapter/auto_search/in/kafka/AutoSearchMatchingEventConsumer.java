package com.vendo.auto_search_service.adapter.auto_search.in.kafka;

import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchMatchingUseCase;
import com.vendo.event_lib.auto_search.AutoSearchMatchingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoSearchMatchingEventConsumer {

    private final AutoSearchMatchingUseCase useCase;

    @KafkaListener(
            topics = "${kafka.events.auto-search.matching.topic}",
            groupId = "${kafka.events.auto-search.matching.groupId}",
            properties = {"auto.offset.reset: ${kafka.events.auto-search.matching.properties.auto-offset-reset}"},
            containerFactory = "${kafka.events.auto-search.matching.container-factory}"
    )
    public void listenAutoSearchMatchingEvent(AutoSearchMatchingEvent event) {
        log.info("Received event for auto search matching: {}.", event);
        useCase.match(event.id());
    }
}
