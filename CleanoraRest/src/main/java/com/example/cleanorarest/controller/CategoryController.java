package com.example.cleanorarest.controller;

import com.example.cleanorarest.model.category.CategoryResponse;
import com.example.cleanorarest.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import com.example.cleanorarest.Exception.CategoryNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus; // Import HttpStatus

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Category", description = "Category management APIs")
@RestController
@RequestMapping("/api/v1/category")
@AllArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/getAllCategoriesList")
    @Operation(summary = "Get all categories as a list", description = "Retrieves a list of all categories.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class, type = "array"))})
    })
    public ResponseEntity<List<CategoryResponse>> getAllCategoryList() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/getAllCategories")
    @Operation(summary = "Get all categories with pagination and search", description = "Retrieves a paginated list of categories. Supports optional search query.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))})
    })
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "") String search,
                                                                   @RequestParam(defaultValue = "5") Integer size) {
        return ResponseEntity.ok(categoryService.getPageAllCategories(page, size, search));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieves a specific category by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Category not found", content = {@Content(mediaType = "application/json", schema = @Schema())})
    })
    public ResponseEntity<?> getCategory(@PathVariable Long id) {
        try {
            CategoryResponse category = categoryService.getCategoryResponseById(id);
            return ResponseEntity.ok(category);
        } catch (CategoryNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
