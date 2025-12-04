package com.navidad.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {
    private String id;
    private String rentId;
    private double amount;
    private LocalDateTime date;

    public Payment() {}

    public Payment(String rentId, double amount) {
        this.id = UUID.randomUUID().toString();
        this.rentId = rentId;
        this.amount = amount;
        this.date = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getRentId() { return rentId; }
    public double getAmount() { return amount; }
    public LocalDateTime getDate() { return date; }
}
