package com.vendo.auto_search_service.adapter.auto_search.out;

import com.vendo.auto_search_service.AutoSearchMatchingEvent;
import com.vendo.auto_search_service.adapter.auto_search.out.kafka.AutoSearchMatchingEventProducer;
import com.vendo.auto_search_service.port.auto_search.AutoSearchEventSenderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutoSearchEventSenderAdapter implements AutoSearchEventSenderPort {

    private final AutoSearchMatchingEventProducer matchingEventProducer;

    @Override
    public void sendMatching(String autoSearchId) {
        AutoSearchMatchingEvent event = AutoSearchMatchingEvent.from(autoSearchId);
        matchingEventProducer.send(event);
    }
}
