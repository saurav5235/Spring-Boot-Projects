package com.example.inventory_service.events;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class OrderCreatedEvent {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private String source;
    private Payload payload;

    @Data
    public static class Payload {
        private String orderId;
        private String customerId;
        private String restaurantId;
        private List<Item> items;
        private Double totalAmount;
        private String paymentMethod;
    }

    @Data
    public static class Item {
        private String itemId;
        private Integer qty;
        private Double price;
    }
}
