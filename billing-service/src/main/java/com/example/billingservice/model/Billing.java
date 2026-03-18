package com.example.billingservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Billing {
    private String billingId;
    private String orderId;
    private double amount;
    private String paymentStatus;
    private String paymentMethod;
}
