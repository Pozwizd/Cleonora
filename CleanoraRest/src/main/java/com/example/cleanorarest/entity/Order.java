package com.example.cleanorarest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull(message = "Дата не может быть пустой")
    private LocalDate startDate;
    @NotNull(message = "Время не может быть пустым")
    private LocalTime startTime;
    private LocalDate endDate;
    private Duration totalDuration;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEW;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderCleaning> orderCleanings = new ArrayList<>();

    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime updated = LocalDateTime.now();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private AddressOrder addressOrder;

    public void addOrderCleaning(OrderCleaning orderCleaning) {
        orderCleanings.add(orderCleaning);
    }

    public void removeOrderCleaning(OrderCleaning orderCleaning) {
        orderCleanings.remove(orderCleaning);
    }

    public void calculateTotalDuration() {
        Duration totalDuration = Duration.ZERO;
        for (OrderCleaning orderCleaning : orderCleanings) {
            totalDuration = totalDuration.plus(orderCleaning.getDurationCleaning());
        }
        this.totalDuration = totalDuration;
    }
    public void calculatePrice() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderCleaning orderCleaning : orderCleanings) {
            totalPrice = totalPrice.add(orderCleaning.getPrice());
        }
        this.price = totalPrice;
    }

    @PrePersist
    @PreUpdate
    private void preSave() {
        calculateTotalDuration();
        calculatePrice();
        updated = LocalDateTime.now();
    }

}