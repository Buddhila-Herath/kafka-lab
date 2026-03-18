package com.example.billingservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BillingService {
    
    private static final Logger logger = LoggerFactory.getLogger(BillingService.class);
    
    // In-memory billing storage
    private final Map<String, Boolean> payments = new HashMap<>();
    
    public boolean processPayment(String orderId, double amount) {
        logger.info("Processing payment for order: {} amount: {}", orderId, amount);
        
        // Simulate payment processing
        boolean paymentSuccess = true; // In real scenario, integrate with payment gateway
        
        payments.put(orderId, paymentSuccess);
        
        if (paymentSuccess) {
            logger.info("Payment successful for order: {}", orderId);
        } else {
            logger.warn("Payment failed for order: {}", orderId);
        }
        
        return paymentSuccess;
    }
    
    public boolean getPaymentStatus(String orderId) {
        return payments.getOrDefault(orderId, false);
    }
}
