package com.example.order_service.controller;

import com.example.order_service.events.OrderCreatedEvent;
import com.example.order_service.outbox.OutboxEvent;
import com.example.order_service.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private static final String TOPIC = "orders.created";

    @Autowired
    private OutboxRepository outboxRepository;



    @PostMapping("/test")
    public ResponseEntity<?> publishTestOrder() {
        OrderCreatedEvent event = OrderCreatedEvent.createSample();
        String key = event.getPayload().getOrderId();

        CompletableFuture<SendResult<String, OrderCreatedEvent>> future =
                kafkaTemplate.send(TOPIC, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("✅ Sent eventId=" + event.getEventId()
                        + " to topic=" + result.getRecordMetadata().topic());
            } else {
                System.err.println("❌ Kafka down, saving to outbox: " + event.getEventId());
                saveToOutbox(event);  // 👈 new helper method
            }
        });

        return ResponseEntity.accepted().body(Map.of(
                "status", "processing",
                "orderId", key,
                "eventId", event.getEventId()
        ));
    }

    private void saveToOutbox(OrderCreatedEvent event) {
        try {
            OutboxEvent outbox = new OutboxEvent();
            outbox.setEventId(event.getEventId());
            outbox.setEventType(event.getEventType());
            outbox.setPayload(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(event));
            outbox.setStatus("PENDING");
            outbox.setCreatedAt(java.time.Instant.now());
            outboxRepository.save(outbox);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


