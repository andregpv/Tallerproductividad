package com.navidad.model;

import java.util.UUID;

public class Client {
    private String id;
    private String name;
    private String phone;

    public Client() { }

    public Client(String name, String phone) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.phone = phone;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return "[" + id.substring(0,6) + "] " + name + " (" + phone + ")";
    }
}
