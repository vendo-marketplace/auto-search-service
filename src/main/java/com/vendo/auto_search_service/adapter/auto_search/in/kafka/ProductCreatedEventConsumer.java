package com.vendo.auto_search_service.adapter.auto_search.in.kafka;

import com.vendo.auto_search_service.adapter.product.mapper.EventProductMapper;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchMatchingUseCase;
import com.vendo.event_lib.product.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCreatedEventConsumer {

    private final EventProductMapper mapper;
    private final AutoSearchMatchingUseCase useCase;

    @KafkaListener(
            topics = "${kafka.events.product.created-event.topic}",
            groupId = "${kafka.events.product.created-event.groupId}",
            properties = {"auto.offset.reset: ${kafka.events.product.created-event.auto-offset-reset}"},
            containerFactory = "${kafka.events.product.created-event.container-factory}"
    )
    public void listenProductCreatedEvent(ProductCreatedEvent event) {
        log.info("Received product created event: {}.", event);
        useCase.matchNew(mapper.toProduct(event));
    }

}
