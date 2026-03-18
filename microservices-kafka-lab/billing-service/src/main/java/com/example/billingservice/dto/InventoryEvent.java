package com.example.billingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent {
    private String eventId;
    private String eventType;
    private Inventory inventory;
    private String orderId;
    private LocalDateTime timestamp;
}
