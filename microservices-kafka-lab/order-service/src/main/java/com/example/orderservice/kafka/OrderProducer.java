package com.example.orderservice.kafka;

import com.example.orderservice.model.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderProducer.class);
    
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    
    @Value("${order.topic}")
    private String orderTopic;
    
    public OrderProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendOrderEvent(OrderEvent event) {
        logger.info("Sending order event: {} to topic: {}", event.getEventId(), orderTopic);
        kafkaTemplate.send(orderTopic, event.getEventId(), event);
    }
}
