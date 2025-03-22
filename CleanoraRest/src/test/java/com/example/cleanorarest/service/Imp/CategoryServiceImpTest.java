package com.example.cleanorarest.service.Imp;

import com.example.cleanorarest.Exception.CategoryNotFoundException;
import com.example.cleanorarest.entity.Category;
import com.example.cleanorarest.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.cleanorarest.mapper.CategoryMapper;
import com.example.cleanorarest.model.category.CategoryResponse;
import com.example.cleanorarest.specification.CategorySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import com.example.cleanorarest.mapper.CategoryMapper;
import com.example.cleanorarest.model.category.CategoryRequest;
import com.example.cleanorarest.model.category.CategoryResponse;
import com.example.cleanorarest.specification.CategorySpecification;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImpTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImp categoryService;

    /**
     * Тест для {@link CategoryServiceImp#save(Category)}.
     * Должен сохранять категорию.
     */
    @Test
    void save_ValidCategory_ReturnsSavedCategory() {
        Category category = new Category();
        category.setName("Test Category");

        when(categoryRepository.save(category)).thenReturn(category);

        Category savedCategory = categoryService.save(category);

        assertNotNull(savedCategory);
        assertEquals("Test Category", savedCategory.getName());

        verify(categoryRepository).save(category);
    }

    /**
     * Тест для {@link CategoryServiceImp#getAllCategories()}.
     * Должен возвращать список всех категорий.
     */
    @Test
    void getAllCategories_ReturnsListOfCategories() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category 1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");

        List<Category> categories = Arrays.asList(category1, category2);

        CategoryResponse response1 = new CategoryResponse();
        response1.setId(1L);
        response1.setName("Category 1");
        CategoryResponse response2 = new CategoryResponse();
        response2.setId(2L);
        response2.setName("Category 2");
        List<CategoryResponse> categoryResponses = Arrays.asList(response1, response2);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toResponseList(categories)).thenReturn(categoryResponses);

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).getName());
        assertEquals("Category 2", result.get(1).getName());

        verify(categoryRepository).findAll();
        verify(categoryMapper).toResponseList(categories);
    }

    /**
     * Тест для {@link CategoryServiceImp#getPageAllCategories(int, Integer, String)}.
     * Должен возвращать страницу категорий с учетом пагинации и поиска.
     */
    @Test
    void getPageAllCategories_ReturnsPageOfCategories() {
        int page = 0;
        int size = 10;
        String search = "test";

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category 1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");

        List<Category> categories = Arrays.asList(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories);

        CategoryResponse response1 = new CategoryResponse();
        response1.setId(1L);
        response1.setName("Category 1");
        CategoryResponse response2 = new CategoryResponse();
        response2.setId(2L);
        response2.setName("Category 2");
        List<CategoryResponse> categoryResponses = Arrays.asList(response1, response2);
        Page<CategoryResponse> expectedResponsePage = new PageImpl<>(categoryResponses);


        when(categoryRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(categoryPage);
        when(categoryMapper.toResponsePage(categoryPage)).thenReturn(expectedResponsePage);

        Page<CategoryResponse> actualResponsePage = categoryService.getPageAllCategories(page, size, search);

        assertEquals(expectedResponsePage.getSize(), actualResponsePage.getSize());
        assertEquals(expectedResponsePage.getContent().size(), actualResponsePage.getContent().size());
        assertEquals(expectedResponsePage.getContent().get(0).getId(), actualResponsePage.getContent().get(0).getId());
        assertEquals(expectedResponsePage.getContent().get(1).getId(), actualResponsePage.getContent().get(1).getId());

        verify(categoryRepository).findAll(any(Specification.class), any(PageRequest.class));
        verify(categoryMapper).toResponsePage(categoryPage);
    }

    /**
     * Тест для {@link CategoryServiceImp#getCategoryById(Long)}.
     * Должен возвращать категорию по ID.
     */
    @Test
    void getCategoryById_ValidId_ReturnsCategory() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Test Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.getCategoryById(categoryId);

        assertTrue(result.isPresent());
        assertEquals(categoryId, result.get().getId());
        assertEquals("Test Category", result.get().getName());

        verify(categoryRepository).findById(categoryId);
    }

    /**
     * Тест для {@link CategoryServiceImp#getCategoryById(Long)}.
     * Должен возвращать пустой Optional, если категория не найдена.
     */
    @Test
    void getCategoryById_InvalidId_ReturnsEmptyOptional() {
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.getCategoryById(categoryId);

        assertFalse(result.isPresent());

        verify(categoryRepository).findById(categoryId);
    }

    /**
     * Тест для {@link CategoryServiceImp#getCategoryResponseById(Long)}.
     * Должен возвращать CategoryResponse по ID.
     */
    @Test
    void getCategoryResponseById_ValidId_ReturnsCategoryResponse() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Test Category");

        CategoryResponse response = new CategoryResponse();
        response.setId(categoryId);
        response.setName("Test Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(response);

        CategoryResponse actualResponse = categoryService.getCategoryResponseById(categoryId);

        assertEquals(response.getId(), actualResponse.getId());
        assertEquals(response.getName(), actualResponse.getName());

        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).toResponse(category);
    }

    /**
     * Тест для {@link CategoryServiceImp#getCategoryResponseById(Long)}.
     * Должен бросать CategoryNotFoundException, если категория не найдена.
     */
    @Test
    void getCategoryResponseById_InvalidId_ThrowsCategoryNotFoundException() {
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryResponseById(categoryId));

        verify(categoryRepository).findById(categoryId);
    }



    /**
     * Тест для {@link CategoryServiceImp#updateCategory(Long, CategoryRequest)}.
     * Должен обновлять существующую категорию.
     */
    @Test
    void updateCategory_ValidIdAndRequest_ReturnsUpdatedCategoryResponse() {
        Long categoryId = 1L;
        CategoryRequest request = new CategoryRequest();
        request.setName("Updated Category");

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("Original Category");

        Category updatedCategory = new Category();
        updatedCategory.setId(categoryId);
        updatedCategory.setName("Updated Category");

        CategoryResponse response = new CategoryResponse();
        response.setId(categoryId);
        response.setName("Updated Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toResponse(updatedCategory)).thenReturn(response);

        CategoryResponse actualResponse = categoryService.updateCategory(categoryId, request);

        assertEquals(response.getId(), actualResponse.getId());
        assertEquals(response.getName(), actualResponse.getName());

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(any(Category.class));
        verify(categoryMapper).partialUpdate(request, existingCategory);
        verify(categoryMapper).toResponse(updatedCategory);
    }

    /**
     * Тест для {@link CategoryServiceImp#updateCategory(Long, CategoryRequest)}.
     * Должен бросать EntityNotFoundException, если категория не найдена.
     */
    @Test
    void updateCategory_InvalidId_ThrowsEntityNotFoundException() {
        Long categoryId = 1L;
        CategoryRequest request = new CategoryRequest();
        request.setName("Updated Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.updateCategory(categoryId, request));

        verify(categoryRepository).findById(categoryId);
    }



    /**
     * Тест для {@link CategoryServiceImp#ifCategoryMoreThan(int)}.
     * Должен возвращать true, если количество категорий больше заданного значения.
     */
    @Test
    void ifCategoryMoreThan_MoreCategories_ReturnsTrue() {
        int threshold = 5;
        when(categoryRepository.count()).thenReturn((long) threshold + 1);

        boolean result = categoryService.ifCategoryMoreThan(threshold);

        assertTrue(result);

        verify(categoryRepository).count();
    }

    /**
     * Тест для {@link CategoryServiceImp#ifCategoryMoreThan(int)}.
     * Должен возвращать false, если количество категорий меньше или равно заданному значению.
     */
    @Test
    void ifCategoryMoreThan_FewerCategories_ReturnsFalse() {
        int threshold = 5;
        when(categoryRepository.count()).thenReturn((long)threshold);

        boolean result = categoryService.ifCategoryMoreThan(threshold);

        assertFalse(result);

        verify(categoryRepository).count();
    }
}
