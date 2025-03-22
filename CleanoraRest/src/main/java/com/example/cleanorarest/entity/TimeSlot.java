package com.example.cleanorarest.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isAvailable = true; // Флаг доступности
    @ManyToOne
    @JoinColumn(name = "order_cleaning_id")
    private OrderCleaning orderCleaning;
    @ManyToOne
    @JoinColumn(name = "workday_id", nullable = false)
    private Workday workday;

    // Метод для расчета продолжительности TimeSlot
    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }
}
