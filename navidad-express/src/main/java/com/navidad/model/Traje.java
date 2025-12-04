package com.navidad.model;

import java.util.UUID;

public class Traje {
    private String id;
    private String name;
    private String color;
    private double price;

    public Traje() { }

    public Traje(String name, String color, double price) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.color = color;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return "[" + id.substring(0,6) + "] " + name + " (" + color + ") - $" + price;
    }
}
