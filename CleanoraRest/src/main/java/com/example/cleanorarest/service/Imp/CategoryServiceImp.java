package com.example.cleanorarest.service.Imp;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.cleanorarest.Exception.CategoryNotFoundException;
import com.example.cleanorarest.entity.Category;
import com.example.cleanorarest.mapper.CategoryMapper;
import com.example.cleanorarest.model.category.CategoryRequest;
import com.example.cleanorarest.model.category.CategoryResponse;
import com.example.cleanorarest.repository.CategoryRepository;
import com.example.cleanorarest.service.CategoryService;
import com.example.cleanorarest.specification.CategorySpecification;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @CacheEvict(value = "categories", allEntries = true)
    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Cacheable(value = "categories")
    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.toResponseList(categoryRepository.findAll());
    }

    @Cacheable(value = "categories")
    @Override
    public Page<CategoryResponse> getPageAllCategories(int page, Integer size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        return categoryMapper.toResponsePage(categoryRepository.findAll(CategorySpecification.search(search), pageRequest));
    }

    @Cacheable(value = "category", key = "#id")
    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Cacheable(value = "category", key = "#id")
    @Override
    public CategoryResponse getCategoryResponseById(Long id){
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty()){
            throw new CategoryNotFoundException("Category with id " + id + " not found");
        }
        return categoryMapper.toResponse(category.get());
    }

    @CacheEvict(value = "category", key = "#id")
    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            throw new EntityNotFoundException("Категория с ID " + id + " не найдена");
        }
        Category categoryToUpdate = existingCategory.get();
        categoryMapper.partialUpdate(categoryRequest, categoryToUpdate);
        Category updatedCategory = categoryRepository.save(categoryToUpdate);
        return categoryMapper.toResponse(updatedCategory);
    }


    @Override
    public boolean ifCategoryMoreThan(int i) {
        return categoryRepository.count() > i;
    }
}
