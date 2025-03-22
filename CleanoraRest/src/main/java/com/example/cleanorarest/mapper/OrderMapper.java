package com.example.cleanorarest.mapper;

import com.example.cleanorarest.entity.Order;
import com.example.cleanorarest.model.order.OrderCleaningRequest;
import com.example.cleanorarest.model.order.OrderRequest;
import com.example.cleanorarest.model.order.OrderResponse;
import com.example.cleanorarest.repository.CleaningRepository;
import com.example.cleanorarest.repository.CustomerRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING, uses = {OrderCleaningMapper.class,
        CustomerRepository.class, CleaningRepository.class})
public interface OrderMapper {



    default Order toEntity(OrderRequest orderRequest) {
        if (orderRequest == null) {
            return null;
        } else {
            Order order = new Order();
            if (orderRequest.getId() != null){
                order.setId(orderRequest.getId());
            }
            order.setStartDate(orderRequest.getStartDate());
            order.setStartTime(orderRequest.getStartTime());
            order.setStatus(orderRequest.getStatus());

            order.calculateTotalDuration();
            return order;
        }
    }

    default OrderResponse toResponse(Order order, OrderCleaningMapper orderCleaningMapper) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setStartDate(order.getStartDate());
        orderResponse.setEndDate(order.getEndDate());
        orderResponse.setTotalDuration(order.getTotalDuration());

        orderResponse.setPrice(order.getPrice());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setStartTime(order.getStartTime());
        orderResponse.setOrderCleanings(orderCleaningMapper.toResponseList(order.getOrderCleanings()));
        orderResponse.setCustomerId(order.getCustomer().getId());
        orderResponse.setCustomerName(order.getCustomer().getName());
        return orderResponse;
    }

    List<OrderResponse> toResponseList(List<Order> orders);

    default Page<OrderResponse> toPageResponse(Page<Order> orderPage,
                                               @Context OrderCleaningMapper orderCleaningMapper) {
        return orderPage.map(order -> toResponse(order, orderCleaningMapper));
    }

}