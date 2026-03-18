package com.example.inventoryservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long orderId;
    private String product;
    private Integer quantity;
    private Double price;
    private String customerName;
    private String customerEmail;
    private LocalDateTime orderDate;
    private String eventType;
}
