package com.example.cleanorarest.model.category;

import com.example.cleanorarest.validators.category.UniqueCategoryName;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for {@link com.example.cleanorarest.entity.Category}
 */
@Data
@UniqueCategoryName
public class CategoryRequest {
    private Long id;
    @Size(min = 2, max = 50)
    private String name;
    @Size(min = 2, max = 100)
    private String description;
}
