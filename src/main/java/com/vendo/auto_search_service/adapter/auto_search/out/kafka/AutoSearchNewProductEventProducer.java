package com.vendo.auto_search_service.adapter.auto_search.out.kafka;

import com.vendo.event_lib.auto_search.AutoSearchNewProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoSearchNewProductEventProducer {

    @Value("${kafka.events.auto-search.new-product-event.topic}")
    private String topic;

    private final KafkaTemplate<String, AutoSearchNewProductEvent> kafkaTemplate;

    public void send(boolean async, AutoSearchNewProductEvent event) {
        CompletableFuture<SendResult<String, AutoSearchNewProductEvent>> result = kafkaTemplate.send(topic, event);
        if (!async) result.join();
        log.info("Sent event for auto search new product: {}.", event);
    }

}
