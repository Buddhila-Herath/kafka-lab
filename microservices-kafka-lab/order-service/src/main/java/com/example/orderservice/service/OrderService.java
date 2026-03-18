package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.kafka.OrderProducer;
import com.example.orderservice.model.OrderEvent;
import com.example.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;
    
    public OrderService(OrderRepository orderRepository, OrderProducer orderProducer) {
        this.orderRepository = orderRepository;
        this.orderProducer = orderProducer;
    }
    
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        // Create order entity
        Order order = new Order();
        order.setProduct(orderRequest.getProduct());
        order.setQuantity(orderRequest.getQuantity());
        order.setPrice(orderRequest.getPrice());
        order.setCustomerName(orderRequest.getCustomerName());
        order.setCustomerEmail(orderRequest.getCustomerEmail());
        
        // Save order to database
        Order savedOrder = orderRepository.save(order);
        logger.info("Order saved with ID: {}", savedOrder.getId());
        
        // Map entity to model for Kafka event
        com.example.orderservice.model.Order orderModel = new com.example.orderservice.model.Order(
            savedOrder.getId().toString(),
            savedOrder.getProduct(),
            savedOrder.getQuantity(),
            savedOrder.getPrice(),
            savedOrder.getOrderStatus(),
            savedOrder.getOrderDate()
        );

        // Create and publish event to Kafka
        OrderEvent orderEvent = new OrderEvent(
            UUID.randomUUID().toString(),
            "ORDER_CREATED",
            orderModel,
            LocalDateTime.now()
        );
        
        orderProducer.sendOrderEvent(orderEvent);
        logger.info("Order event published to Kafka: {}", orderEvent);
        
        // Map to response
        return mapToResponse(savedOrder);
    }
    
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        return mapToResponse(order);
    }
    
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        order.setOrderStatus(status);
        Order updatedOrder = orderRepository.save(order);
        logger.info("Order {} status updated to {}", orderId, status);
        
        return mapToResponse(updatedOrder);
    }
    
    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getProduct(),
            order.getQuantity(),
            order.getPrice(),
            order.getCustomerName(),
            order.getCustomerEmail(),
            order.getOrderDate(),
            order.getOrderStatus()
        );
    }
}
