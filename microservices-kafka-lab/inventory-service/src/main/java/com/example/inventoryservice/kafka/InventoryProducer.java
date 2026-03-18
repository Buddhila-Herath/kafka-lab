package com.example.inventoryservice.kafka;

import com.example.inventoryservice.model.InventoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryProducer.class);
    
    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;
    
    @Value("${inventory.topic}")
    private String inventoryTopic;
    
    public InventoryProducer(KafkaTemplate<String, InventoryEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendInventoryEvent(InventoryEvent event) {
        logger.info("Sending inventory event: {} to topic: {}", event.getEventId(), inventoryTopic);
        kafkaTemplate.send(inventoryTopic, event.getEventId(), event);
    }
}
