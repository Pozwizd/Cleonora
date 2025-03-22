package com.example.cleanorarest.service.Imp;

import com.example.cleanorarest.entity.Order;
import com.example.cleanorarest.repository.OrderCleaningRepository;
import com.example.cleanorarest.repository.OrderRepository;
import com.example.cleanorarest.service.OrderCleaningService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
public class OrderCleaningServiceImp implements OrderCleaningService {

    private final OrderRepository orderRepository;
    private final OrderCleaningRepository orderCleaningRepository;


    @Override
    public boolean ifOrdersMoreThan(int i) {
        return orderRepository.count() > i;
    }

    @Transactional
    public void deleteOrderCleanings(Order existingOrder) {
        orderCleaningRepository.deleteAll(existingOrder.getOrderCleanings());
    }
}
