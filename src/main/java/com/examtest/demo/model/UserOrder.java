package com.examtest.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class UserOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order")
    private List<Booking> bookings;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}