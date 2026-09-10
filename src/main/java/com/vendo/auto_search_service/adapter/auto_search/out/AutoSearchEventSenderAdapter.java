package com.vendo.auto_search_service.adapter.auto_search.out;

import com.vendo.auto_search_service.adapter.auto_search.out.kafka.AutoSearchEmailEventProducer;
import com.vendo.auto_search_service.adapter.auto_search.out.kafka.AutoSearchMatchingEventProducer;
import com.vendo.auto_search_service.adapter.auto_search.out.kafka.AutoSearchNewProductEventProducer;
import com.vendo.auto_search_service.port.auto_search.AutoSearchEventSenderPort;
import com.vendo.event_lib.auto_search.AutoSearchMatchingEvent;
import com.vendo.event_lib.auto_search.AutoSearchNewProductEvent;
import com.vendo.event_lib.auto_search.AutoSearchReadyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutoSearchEventSenderAdapter implements AutoSearchEventSenderPort {

    private final AutoSearchEmailEventProducer emailEventProducer;
    private final AutoSearchMatchingEventProducer matchingEventProducer;
    private final AutoSearchNewProductEventProducer newProductEventProducer;

    @Override
    public void sendMatching(String id, String email) {
        matchingEventProducer.send(AutoSearchMatchingEvent.from(id, email));
    }

    @Override
    public void sendRequestReady(String id, String email) {
        emailEventProducer.send(AutoSearchReadyEvent.from(id, email));
    }

    @Override
    public void sendRequestNewProduct(String id, String email) {
        newProductEventProducer.send(AutoSearchNewProductEvent.from(id, email));
    }
}
