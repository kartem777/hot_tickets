package com.examtest.demo.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String description;
    private int price;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private UserOrder order;
    public Booking() {}
    public Booking(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public UserOrder getOrder() {
        return order;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setOrder(UserOrder order) {
        this.order = order;
    }
}
