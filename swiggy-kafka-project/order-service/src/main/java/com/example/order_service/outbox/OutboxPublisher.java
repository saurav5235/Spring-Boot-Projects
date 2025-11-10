package com.example.order_service.outbox;

import com.example.order_service.events.OrderCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TOPIC = "orders.created";

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findByStatus("PENDING");

        for (OutboxEvent oe : pendingEvents) {
            try {
                OrderCreatedEvent event =
                        objectMapper.readValue(oe.getPayload(), OrderCreatedEvent.class);

                kafkaTemplate.send(TOPIC, event.getPayload().getOrderId(), event)
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                oe.setStatus("SENT");
                                oe.setLastAttemptAt(Instant.now());
                                outboxRepository.save(oe);
                                System.out.println("📤 Retried and sent eventId=" + event.getEventId());
                            } else {
                                oe.setLastAttemptAt(Instant.now());
                                outboxRepository.save(oe);
                                System.err.println("⚠️ Retry failed for " + event.getEventId());
                            }
                        });
            } catch (Exception e) {
                oe.setStatus("FAILED");
                outboxRepository.save(oe);
                e.printStackTrace();
            }
        }
    }
}
