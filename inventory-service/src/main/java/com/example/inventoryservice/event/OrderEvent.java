package com.example.inventoryservice.event;

import com.example.inventoryservice.dto.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String eventId;
    private String eventType;
    private Order order;
    private LocalDateTime timestamp;
}
