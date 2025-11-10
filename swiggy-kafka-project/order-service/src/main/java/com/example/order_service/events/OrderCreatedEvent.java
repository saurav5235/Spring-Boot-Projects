package com.example.order_service.events;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class OrderCreatedEvent {
    private String eventId;
    private String eventType = "OrderCreated";
    private Instant occurredAt;
    private String source;
    private Payload payload;

    @Data
    public static class Payload {
        private String orderId;
        private String clientOrderId;
        private String customerId;
        private String restaurantId;
        private List<Item> items;
        private Double totalAmount;
        private String currency = "INR";
        private String paymentMethod;
    }

    @Data
    public static class Item {
        private String itemId;
        private Integer qty;
        private Double price;
    }

    public static OrderCreatedEvent createSample() {
        // simple test event, just to verify config works
        OrderCreatedEvent e = new OrderCreatedEvent();
        e.eventId = UUID.randomUUID().toString();
        e.occurredAt = Instant.now();
        e.source = "order-api-v1";
        Payload p = new Payload();
        p.setOrderId("ORD-" + System.currentTimeMillis());
        p.setCustomerId("CUST-1");
        p.setRestaurantId("REST-9");
        p.setTotalAmount(200.0);
        p.setPaymentMethod("COD");
        e.payload = p;
        return e;
    }
}
