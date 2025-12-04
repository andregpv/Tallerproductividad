package com.navidad.model;

import java.time.LocalDate;
import java.util.UUID;

public class Rent {
    private String id;
    private String clientId;
    private String trajeId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private double total;
    private double paid;
    private String status; // separado, entregado, regresado

    public Rent() { }

    public Rent(String clientId, String trajeId, LocalDate fromDate, LocalDate toDate, double total) {
        this.id = UUID.randomUUID().toString();
        this.clientId = clientId;
        this.trajeId = trajeId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.total = total;
        this.paid = 0.0;
        this.status = "separado";
    }

    public String getId() { return id; }
    public String getClientId() { return clientId; }
    public String getTrajeId() { return trajeId; }
    public LocalDate getFromDate() { return fromDate; }
    public LocalDate getToDate() { return toDate; }
    public double getTotal() { return total; }
    public double getPaid() { return paid; }
    public String getStatus() { return status; }

    public double getBalance() { return total - paid; }

    public void addPayment(double amount) { this.paid += amount; if (this.paid > this.total) this.paid = this.total; }
    public void setStatus(String status) { this.status = status; }
}
