package com.example.cleanorarest.controller;

import com.example.cleanorarest.model.cleaning.CleaningResponse;
import com.example.cleanorarest.service.CleaningService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cleaning", description = "Cleaning service management APIs")
@RestController
@RequestMapping("/api/v1/cleaning")
@AllArgsConstructor
@Slf4j
public class CleaningController {

    private final CleaningService cleaningService;

    @GetMapping("/getAllServices")
    @Operation(summary = "Get all cleaning services by category", description = "Retrieves a paginated list of cleaning services filtered by category ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))})
    })
    public ResponseEntity<Page<CleaningResponse>> getAllServices(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "5") Integer size,
                                                                 @RequestParam(required = true) Long categoryId) {
        return ResponseEntity.ok(cleaningService.getPageAllCleaningByCategoryId(page, size, categoryId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cleaning service by ID", description = "Retrieves a specific cleaning service by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CleaningResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Service not found", content = {@Content(mediaType = "application/json", schema = @Schema())})
    })
    public ResponseEntity<?> getService(@PathVariable Long id) {
        try {
            CleaningResponse service = cleaningService.getServiceResponseById(id);
            if (service == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(service);
        } catch (Exception e) {
            log.error("Error while getting service by id", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
