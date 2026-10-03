package com.vendo.auto_search_service.adapter.auto_search.out;

import com.vendo.auto_search_service.adapter.auto_search.out.kafka.AutoSearchReadyEventProducer;
import com.vendo.auto_search_service.adapter.auto_search.out.kafka.AutoSearchMatchingEventProducer;
import com.vendo.auto_search_service.adapter.auto_search.out.kafka.AutoSearchNewProductEventProducer;
import com.vendo.auto_search_service.adapter.product.mapper.EventProductMapper;
import com.vendo.auto_search_service.domain.product.Product;
import com.vendo.auto_search_service.port.auto_search.AutoSearchEventSenderPort;
import com.vendo.event_lib.auto_search.AutoSearchMatchingEvent;
import com.vendo.event_lib.auto_search.AutoSearchNewProductEvent;
import com.vendo.event_lib.auto_search.AutoSearchReadyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AutoSearchEventSenderAdapter implements AutoSearchEventSenderPort {

    private final AutoSearchReadyEventProducer readyEventProducer;
    private final AutoSearchMatchingEventProducer matchingEventProducer;
    private final AutoSearchNewProductEventProducer newProductEventProducer;

    private final EventProductMapper eventProductMapper;

    @Override
    public void sendMatching(String id, String email) {
        matchingEventProducer.send(AutoSearchMatchingEvent.from(id, email));
    }

    @Override
    public void sendRequestReady(String id, String email, List<Product> products) {
        readyEventProducer.send(AutoSearchReadyEvent.from(id, email, eventProductMapper.toAutoSearchProductEvents(products)));
    }

    @Override
    public void sendRequestNewProduct(String id, String email, List<Product> products) {
        newProductEventProducer.send(AutoSearchNewProductEvent.from(id, email, eventProductMapper.toAutoSearchProductEvents(products)));
    }
}
