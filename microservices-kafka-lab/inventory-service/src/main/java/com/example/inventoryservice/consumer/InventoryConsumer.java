package com.example.inventoryservice.consumer;

import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.event.OrderEvent;
import com.example.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryConsumer {
    private final InventoryRepository inventoryRepository;
    
    @KafkaListener(topics = "order-topic", groupId = "inventory-group")
    @Transactional
    public void consumeOrderEvent(OrderEvent orderEvent) {
        log.info("Inventory service received order event: {}", orderEvent);
        
        String productId = orderEvent.getOrder().getProductId();
        int quantity = orderEvent.getOrder().getQuantity();

        // Check if inventory exists for the product
        Inventory inventory = inventoryRepository.findByProduct(productId)
            .orElseGet(() -> {
                // Create new inventory for product if not exists
                Inventory newInventory = new Inventory();
                newInventory.setProduct(productId);
                newInventory.setQuantity(100); // Default stock
                newInventory.setReservedQuantity(0);
                return inventoryRepository.save(newInventory);
            });
        
        // Update inventory
        if (inventory.getQuantity() >= quantity) {
            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventoryRepository.save(inventory);
            log.info("Inventory updated for product: {}. Remaining quantity: {}", 
                productId, inventory.getQuantity());
        } else {
            log.warn("Insufficient inventory for product: {}. Available: {}, Requested: {}", 
                productId, inventory.getQuantity(), quantity);
        }
    }
}
