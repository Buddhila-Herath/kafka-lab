package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String product;
    private Integer quantity;
    private Double price;
    private String customerName;
    private String customerEmail;
    private LocalDateTime orderDate;
    private String orderStatus;
}
