package com.example.inventory_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Data
@Table(name = "inventory_orders")
public class InventoryOrder {

    @Id
    private String orderId;

    private String customerId;
    private String restaurantId;
    private Double totalAmount;
    private String paymentMethod;
    private Instant receivedAt;
}
