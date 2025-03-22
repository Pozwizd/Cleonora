package com.example.cleanorarest.model.order;

import com.example.cleanorarest.entity.Order;
import com.example.cleanorarest.entity.OrderStatus;
import com.example.cleanorarest.validators.order.CheckTime;
import com.example.cleanorarest.validators.order.ValidWorkday;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Request model for {@link Order}
 */
@Data
@CheckTime
@ValidWorkday

public class OrderRequest {
    @Schema(example = "null")
    private Long id;
    @Schema(example = "2025-01-01")
    private LocalDate startDate;
    @Schema(example = "10:00")
    private LocalTime startTime;
    @Schema(example = "1")
    private Long customerId;
    @Schema(example = "NEW")
    private OrderStatus status;

    private List<OrderCleaningRequest> orderCleanings;

    @Valid
    private Object address;
}
