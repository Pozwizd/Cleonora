package com.example.cleanorarest.service;


import com.example.cleanorarest.model.order.OrderRequest;
import com.example.cleanorarest.model.order.OrderResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface OrderService {

    OrderResponse createOrder(OrderRequest orderRequest, String email);

    OrderResponse updateOrder(OrderRequest orderRequest, String username);

    Page<OrderResponse> getPageAllOrders(int page, Integer size, String search);

    Page<OrderResponse> getPageAllOrdersLastWeek(int page, Integer size, String search);

    OrderResponse getOrderById(Long id);

    boolean deleteOrder(Long id);

    BigDecimal calculateSalesLastWeek();

    boolean ifOrderMoreThan(int i);

    Integer countCompletedOrders();

    Integer countDailyCompletedOrders();



    BigDecimal calculateTotalSales();

    Page<OrderResponse> getPageAllOrdersByCustomerId(Integer page, Integer size, Long customerId);
}
