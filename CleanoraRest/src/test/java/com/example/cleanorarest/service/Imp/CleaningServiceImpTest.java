package com.example.cleanorarest.service.Imp;

import com.example.cleanorarest.entity.Cleaning;
import com.example.cleanorarest.mapper.CleaningMapper;
import com.example.cleanorarest.model.cleaning.CleaningResponse;
import com.example.cleanorarest.repository.CleaningRepository;
import com.example.cleanorarest.specification.CleaningSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CleaningServiceImpTest {

    @Mock
    private CleaningMapper cleaningMapper;

    @Mock
    private CleaningRepository cleaningRepository;

    @InjectMocks
    private CleaningServiceImp cleaningServiceImp;

    @Test
    void getPageAllCleaningByCategoryId_shouldReturnPageOfCleaningResponses() {
        int page = 0;
        int size = 10;
        Long categoryId = 1L;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Cleaning cleaning = new Cleaning();
        cleaning.setId(1L);
        List<Cleaning> cleaningList = Collections.singletonList(cleaning);
        Page<Cleaning> cleaningPage = new PageImpl<>(cleaningList, pageRequest, 1);
        CleaningResponse cleaningResponse = new CleaningResponse();
        cleaningResponse.setId(1L);
        List<CleaningResponse> cleaningResponseList = Collections.singletonList(cleaningResponse);
        Page<CleaningResponse> cleaningResponsePage = new PageImpl<>(cleaningResponseList, pageRequest, 1);

        when(cleaningRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(cleaningPage);
        when(cleaningMapper.toResponsePage(cleaningPage)).thenReturn(cleaningResponsePage);

        Page<CleaningResponse> result = cleaningServiceImp.getPageAllCleaningByCategoryId(page, size, categoryId);

        assertEquals(cleaningResponsePage, result);
        verify(cleaningRepository).findAll(any(Specification.class), eq(pageRequest));
        verify(cleaningMapper).toResponsePage(cleaningPage);
    }

    @Test
    void getPageAllCleaningByCategoryId_shouldReturnEmptyPageWhenNoCleaningsFound() {
        int page = 0;
        int size = 10;
        Long categoryId = 1L;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        Page<Cleaning> emptyCleaningPage = Page.empty(pageRequest);
        Page<CleaningResponse> emptyCleaningResponsePage = Page.empty(pageRequest);

        when(cleaningRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(emptyCleaningPage);
        when(cleaningMapper.toResponsePage(emptyCleaningPage)).thenReturn(emptyCleaningResponsePage);

        Page<CleaningResponse> result = cleaningServiceImp.getPageAllCleaningByCategoryId(page, size, categoryId);

        assertTrue(result.isEmpty());
        verify(cleaningRepository).findAll(any(Specification.class), eq(pageRequest));
        verify(cleaningMapper).toResponsePage(emptyCleaningPage);
    }

    @Test
    void getServiceResponseById() {
        Long cleaningId = 1L;
        Cleaning cleaning = new Cleaning();
        cleaning.setId(cleaningId);
        cleaning.setName("Test Cleaning");

        CleaningResponse response = new CleaningResponse();
        response.setId(cleaningId);
        response.setName("Test Cleaning");

        when(cleaningRepository.findById(cleaningId)).thenReturn(Optional.of(cleaning));
        when(cleaningMapper.toResponse(cleaning)).thenReturn(response);

        CleaningResponse actualResponse = cleaningServiceImp.getServiceResponseById(cleaningId);

        assertEquals(response.getId(), actualResponse.getId());
        assertEquals(response.getName(), actualResponse.getName());

        verify(cleaningRepository, times(1)).findById(cleaningId);
        verify(cleaningMapper, times(1)).toResponse(cleaning);
    }

    @Test
    void getServiceResponseById_shouldReturnNullWhenCleaningNotFound() {
        Long cleaningId = 1L;

        when(cleaningRepository.findById(cleaningId)).thenReturn(Optional.empty());

        CleaningResponse actualResponse = cleaningServiceImp.getServiceResponseById(cleaningId);

        assertNull(actualResponse);

        verify(cleaningRepository, times(1)).findById(cleaningId);
        verify(cleaningMapper, times(1)).toResponse(null);
    }
    @Test
    void ifServiceMoreThan() {
        int threshold = 5;
        when(cleaningRepository.count()).thenReturn((long) threshold + 1);

        boolean result = cleaningServiceImp.ifServiceMoreThan(threshold);

        assertTrue(result);

        verify(cleaningRepository, times(1)).count();
    }

    @Test
    void save_shouldSaveAndReturnCleaning() {
        Cleaning cleaning = new Cleaning();
        cleaning.setId(1L);
        cleaning.setName("Test Cleaning");

        when(cleaningRepository.save(cleaning)).thenReturn(cleaning);

        Cleaning savedCleaning = cleaningServiceImp.save(cleaning);

        assertEquals(cleaning, savedCleaning);
        verify(cleaningRepository, times(1)).save(cleaning);
    }

    @Test
    void getServiceById_shouldReturnNullWhenCleaningNotFound() {
        Long cleaningId = 1L;

        when(cleaningRepository.findById(cleaningId)).thenReturn(Optional.empty());

        Cleaning cleaning = cleaningServiceImp.getServiceById(cleaningId);

        assertNull(cleaning);
        verify(cleaningRepository).findById(cleaningId);
    }
}
