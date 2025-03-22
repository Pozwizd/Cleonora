package com.example.cleanorarest.mapper;

import com.example.cleanorarest.entity.OrderCleaning;
import com.example.cleanorarest.model.order.OrderCleaningRequest;
import com.example.cleanorarest.model.order.OrderCleaningResponse;
import com.example.cleanorarest.repository.CleaningRepository;
import com.example.cleanorarest.service.CleaningService;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = CleaningRepository.class)
public interface OrderCleaningMapper {

    @Mapping(target = "durationCleaning", ignore = true)
    default OrderCleaning toEntity(OrderCleaningRequest orderCleaningRequest,
                                   @Context CleaningService cleaningService) {

        OrderCleaning orderCleaning = new OrderCleaning();

        if (orderCleaningRequest.getId() != null) {
            orderCleaning.setId(orderCleaningRequest.getId());
        }

        orderCleaning.setNumberUnits(orderCleaningRequest.getNumberUnits());
        orderCleaning.setCleaning(cleaningService.getServiceById(orderCleaningRequest.getCleaningId()));
        orderCleaning.setPrice(BigDecimal.valueOf(
                orderCleaning.getNumberUnits() * orderCleaning.getCleaning().getCleaningSpecifications().getBaseCost()));
        orderCleaning.durationCleaning();
        return orderCleaning;
    }

    default OrderCleaningResponse toResponse(OrderCleaning orderCleaning) {

        OrderCleaningResponse orderCleaningResponse = new OrderCleaningResponse();
        orderCleaningResponse.setId(orderCleaning.getId());
        orderCleaningResponse.setNumberUnits(orderCleaning.getNumberUnits());
        orderCleaningResponse.setCleaningId(orderCleaning.getCleaning().getId());
        orderCleaningResponse.setDurationCleaning(Duration.ofMinutes(orderCleaning.getDurationCleaning().toMinutes()));
        orderCleaningResponse.setPrice(orderCleaning.getPrice());
        return orderCleaningResponse;
    }

    List<OrderCleaningResponse> toResponseList(List<OrderCleaning> orderCleanings);

}