package com.example.cleanorarest.service.Imp;

import com.example.cleanorarest.entity.Customer;
import com.example.cleanorarest.entity.Order;
import com.example.cleanorarest.entity.OrderStatus;
import com.example.cleanorarest.mapper.OrderCleaningMapper;
import com.example.cleanorarest.mapper.OrderMapper;
import com.example.cleanorarest.model.order.OrderCleaningRequest;
import com.example.cleanorarest.model.order.OrderRequest;
import com.example.cleanorarest.model.order.OrderResponse;
import com.example.cleanorarest.repository.OrderRepository;
import com.example.cleanorarest.model.order.CustomerAddressRequest;
import com.example.cleanorarest.repository.TimeSlotRepository;
import com.example.cleanorarest.service.CleaningService;
import com.example.cleanorarest.service.CustomerService;
import com.example.cleanorarest.service.OrderService;
import com.example.cleanorarest.specification.OrderSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@AllArgsConstructor
public class OrderServiceImp implements OrderService {

    private final CustomerService customerService;
    private final OrderMapper orderMapper;
    private final OrderCleaningMapper orderCleaningMapper;
    private final GeoapifyServiceImp geoapifyService;
    private final OrderCleaningSchedulingServiceImp
            orderCleaningSchedulingServiceImp;
    private final CleaningService cleaningService;



    private final OrderRepository orderRepository;
    private final TimeSlotRepository timeSlotRepository;



    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest,
                                     String email) {

        Customer customer = customerService.getCustomerByEmail(email);

        Order order = orderMapper.toEntity(orderRequest);
        order.setCustomer(customer);
        order.setAddressOrder(geoapifyService.processAddress((com.example.cleanorarest.model.order.CustomerAddressRequest) orderRequest.getAddress()));

        for (OrderCleaningRequest orderCleaningRequest : orderRequest.getOrderCleanings()) {
            order.addOrderCleaning(orderCleaningMapper.toEntity(
                    orderCleaningRequest, cleaningService));
        }

        orderCleaningSchedulingServiceImp.createTimeSlotsForOrder(order);

//        order.calculatePrice();

        order.setEndDate(orderCleaningSchedulingServiceImp.calculateEndDate(order));
//        order.calculateTotalDuration();
        return orderMapper.toResponse(orderRepository.save(order), orderCleaningMapper);
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(OrderRequest updatedOrderRequest, String username) {
        Order existingOrder = orderRepository.findById(updatedOrderRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + updatedOrderRequest.getId()));

        timeSlotRepository.deleteAll(existingOrder.getOrderCleanings()
                .stream().flatMap(orderCleaning -> orderCleaning.getTimeSlots().stream()).toList());
        existingOrder.getOrderCleanings().clear();

        for (OrderCleaningRequest orderCleaningRequest : updatedOrderRequest.getOrderCleanings()) {
            existingOrder.addOrderCleaning(orderCleaningMapper.toEntity(
                    orderCleaningRequest, cleaningService));
        }

        existingOrder.setAddressOrder(geoapifyService.processAddress((com.example.cleanorarest.model.order.CustomerAddressRequest) updatedOrderRequest.getAddress()));
        orderCleaningSchedulingServiceImp.createTimeSlotsForOrder(existingOrder);
//        existingOrder.calculatePrice();
        existingOrder.setEndDate(orderCleaningSchedulingServiceImp.calculateEndDate(existingOrder));
//        existingOrder.calculateTotalDuration();
        return orderMapper.toResponse(orderRepository.save(existingOrder), orderCleaningMapper);
    }

    @Override
    public Page<OrderResponse> getPageAllOrders(int page, Integer size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        return orderMapper.toPageResponse(orderRepository.findAll(OrderSpecification.searchByCustomerField(search), pageRequest), orderCleaningMapper);
    }

    @Override
    public Page<OrderResponse> getPageAllOrdersLastWeek(int page, Integer size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        return orderMapper.toPageResponse(orderRepository.findAll(OrderSpecification.updatedWithinLastWeek(), pageRequest), orderCleaningMapper);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        return orderMapper.toResponse(order, orderCleaningMapper);
    }

    @Override
    public boolean deleteOrder(Long id) {
        try {
            orderRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Ошибка при удалении заказа: {}", e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public BigDecimal calculateSalesLastWeek() {
        return orderRepository.findAll(OrderSpecification.updatedWithinLastWeek().and(OrderSpecification.byStatus(OrderStatus.COMPLETED))).stream()
                .map(Order::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean ifOrderMoreThan(int i) {
        return orderRepository.count() > i;
    }

    @Override
    public Integer countCompletedOrders() {
        return orderRepository.countCompletedOrders();
    }

    @Override
    public Integer countDailyCompletedOrders() {
        return orderRepository.findAll(OrderSpecification.orderUpdateLastDaily().and(OrderSpecification.byStatus(OrderStatus.COMPLETED))).size();
    }

    @Override
    public BigDecimal calculateTotalSales() {
        return orderRepository.findAll(OrderSpecification.byStatus(OrderStatus.COMPLETED)).stream()
                .map(Order::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Page<OrderResponse> getPageAllOrdersByCustomerId(Integer page, Integer size, Long customerId) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        return orderMapper.toPageResponse(orderRepository.findAll(OrderSpecification.byCustomerId(customerId), pageRequest), orderCleaningMapper);
    }
}
