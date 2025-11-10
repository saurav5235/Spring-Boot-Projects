package com.example.inventory_service.repository;

import com.example.inventory_service.entity.InventoryOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryOrderRepository extends JpaRepository<InventoryOrder, String> {
}
