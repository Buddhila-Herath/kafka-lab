package com.example.billingservice.kafka;

import com.example.billingservice.model.Billing;
import com.example.billingservice.model.BillingEvent;
import com.example.billingservice.service.BillingService;
import com.example.billingservice.dto.InventoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BillingConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(BillingConsumer.class);
    
    private final BillingService billingService;
    private final BillingProducer billingProducer;
    
    public BillingConsumer(BillingService billingService, BillingProducer billingProducer) {
        this.billingService = billingService;
        this.billingProducer = billingProducer;
    }
    
    @KafkaListener(topics = "${inventory.topic}", groupId = "billing-group")
    public void consumeInventoryEvent(InventoryEvent event) {
        logger.info("Received inventory event: {} of type: {}", event.getEventId(), event.getEventType());
        
        if ("INVENTORY_RESERVED".equals(event.getEventType())) {
            String orderId = event.getOrderId();
            double amount = event.getInventory().getQuantity() * 100.0; // Simple pricing
            
            logger.info("Processing billing for order: {} amount: {}", orderId, amount);
            
            // Process payment
            boolean paymentSuccess = billingService.processPayment(orderId, amount);
            
            Billing billing = new Billing();
            billing.setBillingId(UUID.randomUUID().toString());
            billing.setOrderId(orderId);
            billing.setAmount(amount);
            billing.setPaymentStatus(paymentSuccess ? "PAID" : "FAILED");
            billing.setPaymentMethod("CREDIT_CARD");
            
            // Create and send billing event
            BillingEvent billingEvent = new BillingEvent(
                UUID.randomUUID().toString(),
                paymentSuccess ? "BILLING_COMPLETED" : "BILLING_FAILED",
                billing,
                LocalDateTime.now()
            );
            
            billingProducer.sendBillingEvent(billingEvent);
            
            logger.info("Billing event sent: {} for order: {}", 
                billingEvent.getEventType(), orderId);
        }
    }
}
