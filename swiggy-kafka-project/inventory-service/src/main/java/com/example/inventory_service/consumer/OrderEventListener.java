package com.example.inventory_service.consumer;

import com.example.inventory_service.entity.InventoryOrder;
import com.example.inventory_service.events.OrderCreatedEvent;
import com.example.inventory_service.repository.InventoryOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OrderEventListener {

    @Autowired
    private InventoryOrderRepository repository;

    @KafkaListener(topics = "orders.created", groupId = "inventory-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            System.out.println("📦 Received Order Event: " + event.getEventId());

            // Idempotent save (skip if already exists)
            if (repository.existsById(event.getPayload().getOrderId())) {
                System.out.println("⚠️ Order already processed: " + event.getPayload().getOrderId());
                return;
            }

            InventoryOrder order = new InventoryOrder();
            order.setOrderId(event.getPayload().getOrderId());
            order.setCustomerId(event.getPayload().getCustomerId());
            order.setRestaurantId(event.getPayload().getRestaurantId());
            order.setTotalAmount(event.getPayload().getTotalAmount());
            order.setPaymentMethod(event.getPayload().getPaymentMethod());
            order.setReceivedAt(Instant.now());

            repository.save(order);
            System.out.println("✅ Saved order " + order.getOrderId() + " to inventory DB");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
