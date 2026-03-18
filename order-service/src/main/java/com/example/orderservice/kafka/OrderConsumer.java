package com.example.orderservice.kafka;

import com.example.orderservice.model.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);
    
    @KafkaListener(topics = "${billing.topic}", groupId = "order-group")
    public void consumeBillingEvent(OrderEvent event) {
        logger.info("Received billing event: {} of type: {}", event.getEventId(), event.getEventType());
        
        if ("BILLING_COMPLETED".equals(event.getEventType())) {
            logger.info("Order {} billing completed, status: {}", 
                event.getOrder().getOrderId(), 
                event.getOrder().getStatus());
        } else if ("BILLING_FAILED".equals(event.getEventType())) {
            logger.warn("Order {} billing failed", event.getOrder().getOrderId());
        }
    }
}
