package com.example.billingservice.kafka;

import com.example.billingservice.model.BillingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BillingProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(BillingProducer.class);
    
    private final KafkaTemplate<String, BillingEvent> kafkaTemplate;
    
    @Value("${billing.topic}")
    private String billingTopic;
    
    public BillingProducer(KafkaTemplate<String, BillingEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendBillingEvent(BillingEvent event) {
        logger.info("Sending billing event: {} to topic: {}", event.getEventId(), billingTopic);
        kafkaTemplate.send(billingTopic, event.getEventId(), event);
    }
}
