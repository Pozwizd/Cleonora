package com.example.cleanorarest.service.Imp;

import com.example.cleanorarest.entity.Order;
import com.example.cleanorarest.mapper.OrderCleaningMapper;
import com.example.cleanorarest.mapper.OrderMapper;
import com.example.cleanorarest.model.order.OrderRequest;
import com.example.cleanorarest.model.order.OrderResponse;
import com.example.cleanorarest.repository.CleaningRepository;
import com.example.cleanorarest.repository.CustomerRepository;
import com.example.cleanorarest.repository.OrderRepository;
import com.example.cleanorarest.repository.TimeSlotRepository;
import com.example.cleanorarest.service.CleaningService;
import com.example.cleanorarest.service.CustomerService;
import com.example.cleanorarest.model.order.CustomerAddressRequest;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImpTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CleaningService cleaningService;
    @Mock
    private CustomerService customerService;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderCleaningMapper orderCleaningMapper;
    @Mock
    private GeoapifyServiceImp geoapifyService;
    @Mock
    private OrderCleaningSchedulingServiceImp orderCleaningSchedulingServiceImp;
    @Mock
    private TimeSlotRepository timeSlotRepository;


    private OrderServiceImp orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImp(customerService,
                orderMapper,
                orderCleaningMapper,
                geoapifyService,
                orderCleaningSchedulingServiceImp,
                cleaningService,
                orderRepository,
                timeSlotRepository);
    }

@Test
    void createOrder_shouldCreateOrderSuccessfully() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        com.example.cleanorarest.model.order.OrderCleaningRequest orderCleaningRequest = new com.example.cleanorarest.model.order.OrderCleaningRequest();
        orderRequest.setOrderCleanings(java.util.List.of(orderCleaningRequest));
        CustomerAddressRequest customerAddressRequest = mock(CustomerAddressRequest.class);
        orderRequest.setAddress(customerAddressRequest);
        Order order = new Order();
        OrderResponse orderResponse = new OrderResponse();
        String email = "test@example.com";

        when(customerService.getCustomerByEmail(email)).thenReturn(null); // Mock getCustomerByEmail
        when(orderMapper.toEntity(orderRequest)).thenReturn(order);
        when(geoapifyService.processAddress(any())).thenReturn(null); // Mock to return null for simplicity
        doNothing().when(orderCleaningSchedulingServiceImp).createTimeSlotsForOrder(order);
        when(orderCleaningSchedulingServiceImp.calculateEndDate(order)).thenReturn(null); // Mock to return null
        com.example.cleanorarest.entity.OrderCleaning orderCleaning = new com.example.cleanorarest.entity.OrderCleaning();
        orderCleaning.setPrice(BigDecimal.TEN);
        when(orderCleaningMapper.toEntity(org.mockito.ArgumentMatchers.any(com.example.cleanorarest.model.order.OrderCleaningRequest.class), org.mockito.ArgumentMatchers.any(com.example.cleanorarest.service.CleaningService.class))).thenReturn(orderCleaning);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order, orderCleaningMapper)).thenReturn(orderResponse);

        // Act
        OrderResponse result = orderService.createOrder(orderRequest, email);

        // Assert
        verify(customerService).getCustomerByEmail(email);
        verify(orderMapper).toEntity(orderRequest);
        verify(geoapifyService).processAddress(any());
        verify(orderCleaningSchedulingServiceImp).createTimeSlotsForOrder(order);
        verify(orderCleaningSchedulingServiceImp).calculateEndDate(order);
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrder_shouldUpdateOrderSuccessfully() {
        // Arrange
        OrderRequest updatedOrderRequest = new OrderRequest();
        com.example.cleanorarest.model.order.OrderCleaningRequest orderCleaningRequest = new com.example.cleanorarest.model.order.OrderCleaningRequest();
        updatedOrderRequest.setOrderCleanings(java.util.List.of(orderCleaningRequest));
        CustomerAddressRequest customerAddressRequest2 = mock(CustomerAddressRequest.class);
        updatedOrderRequest.setAddress(customerAddressRequest2);
        updatedOrderRequest.setId(1L);
        Order existingOrder = new Order();
        OrderResponse orderResponse = new OrderResponse();
        String username = "testuser";

        when(orderRepository.findById(updatedOrderRequest.getId())).thenReturn(java.util.Optional.of(existingOrder));
        when(geoapifyService.processAddress(any())).thenReturn(null);
        doNothing().when(orderCleaningSchedulingServiceImp).createTimeSlotsForOrder(existingOrder);
        com.example.cleanorarest.entity.OrderCleaning orderCleaning = new com.example.cleanorarest.entity.OrderCleaning();
        orderCleaning.setPrice(BigDecimal.TEN);
        when(orderCleaningMapper.toEntity(org.mockito.ArgumentMatchers.any(com.example.cleanorarest.model.order.OrderCleaningRequest.class), org.mockito.ArgumentMatchers.any(com.example.cleanorarest.service.CleaningService.class))).thenReturn(orderCleaning);
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);
        when(orderMapper.toResponse(existingOrder, orderCleaningMapper)).thenReturn(orderResponse);

        // Act
        OrderResponse result = orderService.updateOrder(updatedOrderRequest, username);

        // Assert
        verify(orderRepository).findById(updatedOrderRequest.getId());
        verify(geoapifyService).processAddress(any());
        verify(timeSlotRepository).deleteAll(anyList());
        verify(orderCleaningSchedulingServiceImp).createTimeSlotsForOrder(existingOrder);
        verify(orderCleaningMapper).toEntity(org.mockito.ArgumentMatchers.any(com.example.cleanorarest.model.order.OrderCleaningRequest.class), org.mockito.ArgumentMatchers.any(com.example.cleanorarest.service.CleaningService.class));
        verify(orderRepository).save(existingOrder);
        verify(orderMapper).toResponse(existingOrder, orderCleaningMapper);

    }

    @Test
    void getOrderById_shouldReturnOrder_whenOrderExists() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        OrderResponse orderResponse = new OrderResponse();

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(orderMapper.toResponse(order, orderCleaningMapper)).thenReturn(orderResponse);

        // Act
        OrderResponse result = orderService.getOrderById(orderId);

        // Assert
        verify(orderRepository).findById(orderId);
        verify(orderMapper).toResponse(order, orderCleaningMapper);
    }

     @Test
    void getOrderById_shouldThrowException_whenOrderDoesNotExist() {
        // Arrange
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> {
            orderService.getOrderById(orderId);
        });

        verify(orderRepository).findById(orderId);
    }

    @Test
    void deleteOrder_shouldReturnTrue_whenDeletionIsSuccessful() {
        // Arrange
        Long orderId = 1L;
        doNothing().when(orderRepository).deleteById(orderId);

        // Act
        boolean result = orderService.deleteOrder(orderId);

        // Assert
        verify(orderRepository).deleteById(orderId);
        org.junit.jupiter.api.Assertions.assertTrue(result);
    }

    @Test
    void deleteOrder_shouldReturnFalse_whenDeletionFails() {
        // Arrange
        Long orderId = 1L;
        doThrow(new RuntimeException("Deletion failed")).when(orderRepository).deleteById(orderId);

        // Act
        boolean result = orderService.deleteOrder(orderId);

        // Assert
        verify(orderRepository).deleteById(orderId);
        org.junit.jupiter.api.Assertions.assertFalse(result);
    }

    @Test
    void getPageAllOrders_shouldReturnPageOfOrders() {
        // Arrange
        int page = 0;
        int size = 10;
        String search = "";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<Order> orderPage = new org.springframework.data.domain.PageImpl<>(java.util.List.of(new Order()), pageRequest, 1);
        Page<OrderResponse> orderResponsePage = new org.springframework.data.domain.PageImpl<>(java.util.List.of(new OrderResponse()), pageRequest, 1);


        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageRequest))).thenReturn(orderPage);
        when(orderMapper.toPageResponse(orderPage, orderCleaningMapper)).thenReturn(orderResponsePage);

        // Act
        Page<OrderResponse> result = orderService.getPageAllOrders(page, size, search);

        // Assert
        verify(orderRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageRequest));
        verify(orderMapper).toPageResponse(orderPage, orderCleaningMapper);
    }

     @Test
    void getPageAllOrdersLastWeek_shouldReturnPageOfOrders() {
        // Arrange
        int page = 0;
        int size = 10;
        String search = "";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<Order> orderPage = new org.springframework.data.domain.PageImpl<>(java.util.List.of(new Order()), pageRequest, 1);
        Page<OrderResponse> orderResponsePage = new org.springframework.data.domain.PageImpl<>(java.util.List.of(new OrderResponse()), pageRequest, 1);

        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageRequest))).thenReturn(orderPage);
        when(orderMapper.toPageResponse(orderPage, orderCleaningMapper)).thenReturn(orderResponsePage);

        // Act
        Page<OrderResponse> result = orderService.getPageAllOrdersLastWeek(page, size, search);

        // Assert
        verify(orderRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageRequest));
        verify(orderMapper).toPageResponse(orderPage, orderCleaningMapper);
    }

    @Test
    void calculateSalesLastWeek_shouldReturnCorrectSalesAmount() {
        // Arrange
        Order order1 = new Order();
        order1.setPrice(BigDecimal.valueOf(100));
        Order order2 = new Order();
        order2.setPrice(BigDecimal.valueOf(200));
        List<Order> orders = java.util.List.of(order1, order2);

        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(orders);

        // Act
        BigDecimal result = orderService.calculateSalesLastWeek();

        // Assert
        verify(orderRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class));
        org.junit.jupiter.api.Assertions.assertEquals(BigDecimal.valueOf(300), result);
    }

    @Test
    void ifOrderMoreThan_shouldReturnTrue_whenOrderCountIsGreaterThanGivenValue() {
        // Arrange
        int i = 5;
        when(orderRepository.count()).thenReturn(6L);
        // Act
        boolean result = orderService.ifOrderMoreThan(i);

        // Assert
        verify(orderRepository).count();
        org.junit.jupiter.api.Assertions.assertTrue(result);
    }

    @Test
    void ifOrderMoreThan_shouldReturnFalse_whenOrderCountIsLessThanGivenValue() {
        // Arrange
        int i = 5;
        when(orderRepository.count()).thenReturn(4L);
        // Act
        boolean result = orderService.ifOrderMoreThan(i);

        // Assert
        verify(orderRepository).count();
        org.junit.jupiter.api.Assertions.assertFalse(result);
    }

    @Test
    void countCompletedOrders_shouldReturnCorrectCount(){
        // Arrange
        when(orderRepository.countCompletedOrders()).thenReturn(5);

        // Act
        Integer result = orderService.countCompletedOrders();

        //Assert
        verify(orderRepository).countCompletedOrders();
        org.junit.jupiter.api.Assertions.assertEquals(5, result);
    }

    @Test
    void countDailyCompletedOrders_shouldReturnCorrectCount(){
        // Arrange
        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(java.util.List.of(new Order(), new Order()));

        // Act
        Integer result = orderService.countDailyCompletedOrders();

        //Assert
        verify(orderRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class));
        org.junit.jupiter.api.Assertions.assertEquals(2, result);
    }

    @Test
    void calculateTotalSales_shouldReturnCorrectAmount(){
         // Arrange
        Order order1 = new Order();
        order1.setPrice(BigDecimal.valueOf(100));
        Order order2 = new Order();
        order2.setPrice(BigDecimal.valueOf(200));
        List<Order> orders = java.util.List.of(order1, order2);

        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(orders);

        // Act
        BigDecimal result = orderService.calculateTotalSales();

        // Assert
        verify(orderRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class));
        org.junit.jupiter.api.Assertions.assertEquals(BigDecimal.valueOf(300), result);
    }

    @Test
    void getPageAllOrdersByCustomerId_shouldReturnPageOfOrders(){
        // Arrange
        int page = 0;
        int size = 10;
        Long customerId = 1L;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<Order> orderPage = new org.springframework.data.domain.PageImpl<>(java.util.List.of(new Order()), pageRequest, 1);
        Page<OrderResponse> orderResponsePage = new org.springframework.data.domain.PageImpl<>(java.util.List.of(new OrderResponse()), pageRequest, 1);

        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageRequest))).thenReturn(orderPage);
        when(orderMapper.toPageResponse(orderPage, orderCleaningMapper)).thenReturn(orderResponsePage);

        // Act
        Page<OrderResponse> result = orderService.getPageAllOrdersByCustomerId(page, size, customerId);

        // Assert
        verify(orderRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageRequest));
        verify(orderMapper).toPageResponse(orderPage, orderCleaningMapper);

    }
}
