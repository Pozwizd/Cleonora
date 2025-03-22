package com.example.cleanorarest.model.category;

import lombok.Data;

/**
 * DTO for {@link com.example.cleanorarest.entity.Category}
 */
@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
}
