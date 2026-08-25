package com.vendo.auto_search_service.adapter.auto_search.out;

import com.vendo.auto_search_service.adapter.auto_search.out.kafka.AutoSearchEmailEventProducer;
import com.vendo.auto_search_service.adapter.auto_search.out.kafka.AutoSearchMatchingEventProducer;
import com.vendo.auto_search_service.port.auto_search.AutoSearchEventSenderPort;
import com.vendo.event_lib.auto_search.AutoSearchEmailEvent;
import com.vendo.event_lib.auto_search.AutoSearchMatchingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutoSearchEventSenderAdapter implements AutoSearchEventSenderPort {

    private final AutoSearchMatchingEventProducer matchingEventProducer;
    private final AutoSearchEmailEventProducer emailEventProducer;

    @Override
    public void sendMatching(String id) {
        matchingEventProducer.send(AutoSearchMatchingEvent.from(id));
    }

    @Override
    public void sendRequestReady(String id, String email) {
        emailEventProducer.send(AutoSearchEmailEvent.from(id, email));
    }
}
