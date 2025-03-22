package com.example.cleanorarest.controller;

import com.example.cleanorarest.model.cleaning.CleaningResponse;
import com.example.cleanorarest.service.CleaningService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import com.example.cleanorarest.Exception.CategoryNotFoundException;
import com.example.cleanorarest.model.cleaning.CleaningResponse;
import com.example.cleanorarest.service.CleaningService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CleaningControllerTest {

    @Mock
    private CleaningService cleaningService;

    @InjectMocks
    private CleaningController cleaningController;

    @Test
    void getAllServices_ReturnsPageOfCleaningResponses() {

        Page<CleaningResponse> expectedPage = new PageImpl<>(Collections.emptyList());
        when(cleaningService.getPageAllCleaningByCategoryId(0, 5, null)).thenReturn(expectedPage);

        ResponseEntity<Page<CleaningResponse>> response = cleaningController.getAllServices(0, 5, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
    }

    @Test
    void getAllServices_WithCategory_ReturnsPageOfCleaningResponses() {

        Long categoryId = 1L;
        Page<CleaningResponse> expectedPage = new PageImpl<>(Collections.emptyList());
        when(cleaningService.getPageAllCleaningByCategoryId(0, 5, categoryId)).thenReturn(expectedPage);

        ResponseEntity<Page<CleaningResponse>> response = cleaningController.getAllServices(0, 5, categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
    }

    @Test
    void getAllServices_DifferentPageSize_ReturnsPageOfCleaningResponses() {

        int pageSize = 10;
        Page<CleaningResponse> expectedPage = new PageImpl<>(Collections.emptyList());
        when(cleaningService.getPageAllCleaningByCategoryId(0, pageSize, null)).thenReturn(expectedPage);

        ResponseEntity<Page<CleaningResponse>> response = cleaningController.getAllServices(0, pageSize, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
    }

    @Test
    void getService_ExistingId_ReturnsCleaningResponse() {

        Long serviceId = 1L;
        CleaningResponse expectedService = mock(CleaningResponse.class);
        when(cleaningService.getServiceResponseById(serviceId)).thenReturn(expectedService);

        ResponseEntity<?> response = cleaningController.getService(serviceId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedService, response.getBody());
    }

    @Test
    void getService_NonExistingId_ReturnsNotFound() {

        Long serviceId = 1L;
        when(cleaningService.getServiceResponseById(serviceId)).thenReturn(null);

        ResponseEntity<?> response = cleaningController.getService(serviceId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getService_Exception_ReturnsInternalServerError() {
        Long serviceId = 1L;
        when(cleaningService.getServiceResponseById(serviceId)).thenThrow(new RuntimeException("Service exception"));

        ResponseEntity<?> response = cleaningController.getService(serviceId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
