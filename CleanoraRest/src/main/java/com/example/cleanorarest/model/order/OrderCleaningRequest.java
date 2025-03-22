package com.example.cleanorarest.model.order;

import lombok.Data;

/**
 * Request model for {@link OrderCleaning}
 */

/**
 OrderCleaningRequest
- id: Long
- numberUnits: Integer
- cleaningId: Long
 */
@Data
public class OrderCleaningRequest {
    private Long id;
    private Integer numberUnits;
    private Long cleaningId;
}