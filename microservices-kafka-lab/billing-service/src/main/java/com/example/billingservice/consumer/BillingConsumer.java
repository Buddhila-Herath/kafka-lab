package com.example.billingservice.consumer;

import com.example.billingservice.entity.Invoice;
import com.example.billingservice.event.OrderEvent;
import com.example.billingservice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingConsumer {
    private final InvoiceRepository invoiceRepository;
    
    @KafkaListener(topics = "order-topic", groupId = "billing-group")
    @Transactional
    public void consumeOrderEvent(OrderEvent orderEvent) {
        log.info("Billing service received order event: {}", orderEvent);
        
        // Create invoice
        Invoice invoice = new Invoice();
        invoice.setOrderId(orderEvent.getOrderId());
        invoice.setProduct(orderEvent.getProduct());
        invoice.setQuantity(orderEvent.getQuantity());
        invoice.setPrice(orderEvent.getPrice());
        invoice.setTotalAmount(orderEvent.getPrice() * orderEvent.getQuantity());
        invoice.setCustomerName(orderEvent.getCustomerName());
        invoice.setCustomerEmail(orderEvent.getCustomerEmail());
        invoice.setPaymentStatus("PENDING");
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created with ID: {} for Order ID: {}", savedInvoice.getId(), orderEvent.getOrderId());
    }
}
