package com.example.cleanorarest.service;

import com.example.cleanorarest.entity.Category;
import com.example.cleanorarest.model.category.CategoryRequest;
import com.example.cleanorarest.model.category.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;



public interface CategoryService {


    Category save(Category category);
    List<CategoryResponse > getAllCategories();
    Page<CategoryResponse> getPageAllCategories(int page, Integer size, String search);

    Optional<Category> getCategoryById(Long id);

    CategoryResponse getCategoryResponseById(Long id);

    CategoryResponse updateCategory(Long id, @Valid CategoryRequest categoryRequest);


    boolean ifCategoryMoreThan(int i);
}
