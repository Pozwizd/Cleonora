package com.example.cleanorarest.controller;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.cleanorarest.Exception.CategoryNotFoundException;
import com.example.cleanorarest.model.category.CategoryResponse;
import com.example.cleanorarest.service.CategoryService;


@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;


    @Test
    void getAllCategoryList_ReturnsCategoryListAndOkStatus() {

        List<CategoryResponse> categoryList = Arrays.asList(new CategoryResponse(), new CategoryResponse());
        when(categoryService.getAllCategories()).thenReturn(categoryList);

        ResponseEntity<List<CategoryResponse>> response = categoryController.getAllCategoryList();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoryList, response.getBody());
    }

    /**
     * Тест для {@link CategoryController#getAllCategories(int, String, Integer)}.
     * Должен возвращать страницу категорий и OK статус.
     */
    @Test
    void getAllCategories_ReturnsPageOfCategoriesAndOkStatus() {

        Page<CategoryResponse> categoryPage = new PageImpl<>(Arrays.asList(new CategoryResponse(), new CategoryResponse()));
        when(categoryService.getPageAllCategories(0, 5, "")).thenReturn(categoryPage);

        ResponseEntity<Page<CategoryResponse>> response = categoryController.getAllCategories(0, "", 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoryPage, response.getBody());
    }


    @Test
    void getCategory_ReturnsCategoryAndOkStatus() {
        CategoryResponse categoryResponse = new CategoryResponse();
        when(categoryService.getCategoryResponseById(1L)).thenReturn(categoryResponse);

        ResponseEntity<?> response = categoryController.getCategory(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoryResponse, response.getBody());
    }


    @Test
    void getCategory_CategoryNotFound_ReturnsNotFoundStatus() {
        when(categoryService.getCategoryResponseById(1L)).thenThrow(new CategoryNotFoundException("Category not found"));

        ResponseEntity<?> response = categoryController.getCategory(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
