package com.example.billingservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingEvent {
    private String eventId;
    private String eventType;
    private Billing billing;
    private LocalDateTime timestamp;
}
