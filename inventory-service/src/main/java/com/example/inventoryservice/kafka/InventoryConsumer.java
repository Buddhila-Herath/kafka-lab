package com.example.inventoryservice.kafka;

import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.model.InventoryEvent;
import com.example.inventoryservice.service.InventoryService;
import com.example.inventoryservice.event.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InventoryConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryConsumer.class);
    
    private final InventoryService inventoryService;
    private final InventoryProducer inventoryProducer;
    
    public InventoryConsumer(InventoryService inventoryService, InventoryProducer inventoryProducer) {
        this.inventoryService = inventoryService;
        this.inventoryProducer = inventoryProducer;
    }
    
    @KafkaListener(topics = "${order.topic}", groupId = "inventory-group")
    public void consumeOrderEvent(OrderEvent event) {
        logger.info("Received order event: {} of type: {}", event.getEventId(), event.getEventType());
        
        if ("ORDER_CREATED".equals(event.getEventType())) {
            String productId = event.getOrder().getProductId();
            int quantity = event.getOrder().getQuantity();
            
            logger.info("Processing order: {} for product: {} quantity: {}", 
                event.getOrder().getOrderId(), productId, quantity);
            
            // Check inventory availability
            boolean available = inventoryService.checkAvailability(productId, quantity);
            
            Inventory inventory = new Inventory();
            inventory.setProductId(productId);
            inventory.setQuantity(quantity);
            inventory.setAvailable(available);
            
            // Create and send inventory event
            InventoryEvent inventoryEvent = new InventoryEvent(
                UUID.randomUUID().toString(),
                available ? "INVENTORY_RESERVED" : "INVENTORY_FAILED",
                inventory,
                event.getOrder().getOrderId(),
                LocalDateTime.now()
            );
            
            inventoryProducer.sendInventoryEvent(inventoryEvent);
            
            logger.info("Inventory event sent: {} for order: {}", 
                inventoryEvent.getEventType(), event.getOrder().getOrderId());
        }
    }
}
