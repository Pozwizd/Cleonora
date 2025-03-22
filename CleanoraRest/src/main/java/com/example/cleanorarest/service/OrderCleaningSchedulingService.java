package com.example.cleanorarest.service;

import com.example.cleanorarest.entity.Order;

public interface OrderCleaningSchedulingService {

    void createTimeSlotsForOrder(Order order);

    java.time.LocalDate calculateEndDate(Order order);
}
