package com.example.cleanorarest.mapper;

import com.example.cleanorarest.entity.Category;
import com.example.cleanorarest.model.category.CategoryRequest;
import com.example.cleanorarest.model.category.CategoryResponse;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    @Mapping(source = "name", target = "name")
    Category toEntity(CategoryRequest categoryRequest);

    @Mapping(source = "name", target = "name")
    CategoryRequest toRequest(Category category);

    @Mapping(source = "name", target = "name")
    Category toEntity(CategoryResponse categoryResponse);

    @Mapping(source = "name", target = "name")
    CategoryResponse toResponse(Category category);

    default Page<CategoryResponse> toResponsePage(Page<Category> categoryPage){
        return categoryPage.map(this::toResponse);
    }
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Category partialUpdate(CategoryRequest categoryDto, @MappingTarget Category category);

    List<CategoryResponse> toResponseList(List<Category> all);
}