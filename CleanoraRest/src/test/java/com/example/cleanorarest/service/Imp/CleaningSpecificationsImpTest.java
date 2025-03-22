package com.example.cleanorarest.service.Imp;

import com.example.cleanorarest.UploadFile;
import com.example.cleanorarest.entity.CleaningSpecifications;
import com.example.cleanorarest.mapper.CleaningSpecificationsMapper;
import com.example.cleanorarest.model.serviceSpecifications.ServiceSpecificationsRequest;
import com.example.cleanorarest.model.serviceSpecifications.ServiceSpecificationsResponse;
import com.example.cleanorarest.model.serviceSpecifications.ServiceSpecificationsResponse;
import com.example.cleanorarest.repository.ServiceSpecificationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.data.jpa.domain.Specification;
import org.mockito.Mock;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CleaningSpecificationsImp}.
 */
@ExtendWith(MockitoExtension.class)
class CleaningSpecificationsImpTest {

    @Mock
    private ServiceSpecificationsRepository serviceSpecificationsRepository;

    @Mock
    private CleaningSpecificationsMapper cleaningSpecificationsMapper;

    @InjectMocks
    private CleaningSpecificationsImp cleaningSpecificationsImp;

    /**
     * Test for {@link CleaningSpecificationsImp#save(CleaningSpecifications)}.
     */
    @Test
    void save_ShouldSaveAndReturnCleaningSpecifications() {

        CleaningSpecifications cleaningSpecifications = new CleaningSpecifications();
        cleaningSpecifications.setId(1L);
        cleaningSpecifications.setName("Test Specification");

        when(serviceSpecificationsRepository.save(cleaningSpecifications)).thenReturn(cleaningSpecifications);

        CleaningSpecifications savedSpecifications = cleaningSpecificationsImp.save(cleaningSpecifications);

        assertEquals(cleaningSpecifications, savedSpecifications);
        verify(serviceSpecificationsRepository, times(1)).save(cleaningSpecifications);
    }

    /**
     * Test for {@link CleaningSpecificationsImp#getAllServiceSpecifications()}.
     */
    @Test
    void getAllServiceSpecifications_ShouldReturnListOfServiceSpecificationsResponses() {
        CleaningSpecifications cleaningSpecifications = new CleaningSpecifications();
        cleaningSpecifications.setId(1L);
        List<CleaningSpecifications> cleaningSpecificationsList = Collections.singletonList(cleaningSpecifications);

        ServiceSpecificationsResponse serviceSpecificationsResponse = new ServiceSpecificationsResponse();
        serviceSpecificationsResponse.setId(1L);
        List<ServiceSpecificationsResponse> expectedResponseList = Collections.singletonList(serviceSpecificationsResponse);

        when(serviceSpecificationsRepository.findAll()).thenReturn(cleaningSpecificationsList);
        when(cleaningSpecificationsMapper.toResponse(cleaningSpecifications)).thenReturn(serviceSpecificationsResponse);

        List<ServiceSpecificationsResponse> actualResponseList = cleaningSpecificationsImp.getAllServiceSpecifications();

        assertEquals(expectedResponseList.size(), actualResponseList.size());
        assertEquals(expectedResponseList.get(0).getId(), actualResponseList.get(0).getId());
        verify(serviceSpecificationsRepository, times(1)).findAll();
        verify(cleaningSpecificationsMapper, times(1)).toResponse(cleaningSpecifications);
    }


    @Test
    void getAllServiceSpecifications_WithPaginationAndSearch_ShouldReturnPageOfServiceSpecificationsResponses() {
        int page = 0;
        int size = 10;
        String search = "test";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        CleaningSpecifications cleaningSpecifications = new CleaningSpecifications();
        cleaningSpecifications.setId(1L);
        List<CleaningSpecifications> cleaningSpecificationsList = Collections.singletonList(cleaningSpecifications);
        Page<CleaningSpecifications> cleaningSpecificationsPage = new PageImpl<>(cleaningSpecificationsList, pageRequest, 1);
        ServiceSpecificationsResponse serviceSpecificationsResponse = new ServiceSpecificationsResponse();
        serviceSpecificationsResponse.setId(1L);
        List<ServiceSpecificationsResponse> serviceSpecificationsResponseList = Collections.singletonList(serviceSpecificationsResponse);
        Page<ServiceSpecificationsResponse> expectedResponsePage = new PageImpl<>(serviceSpecificationsResponseList, pageRequest, 1);

        when(serviceSpecificationsRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(cleaningSpecificationsPage);
        when(cleaningSpecificationsMapper.toResponsePage(cleaningSpecificationsPage)).thenReturn(expectedResponsePage);

        Page<ServiceSpecificationsResponse> actualResponsePage = cleaningSpecificationsImp.getAllServiceSpecifications(page, size, search);

        assertEquals(expectedResponsePage, actualResponsePage);
        verify(serviceSpecificationsRepository).findAll(any(Specification.class), eq(pageRequest));
        verify(cleaningSpecificationsMapper).toResponsePage(cleaningSpecificationsPage);
    }

    /**
     * Test for {@link CleaningSpecificationsImp#getServiceSpecificationsById(Long)}.
     */
    @Test
    void getServiceSpecificationsById_ShouldReturnOptionalOfCleaningSpecifications() {
        // Arrange
        Long id = 1L;
        CleaningSpecifications cleaningSpecifications = new CleaningSpecifications();
        cleaningSpecifications.setId(id);
        Optional<CleaningSpecifications> expectedOptional = Optional.of(cleaningSpecifications);

        when(serviceSpecificationsRepository.findById(id)).thenReturn(expectedOptional);

        Optional<CleaningSpecifications> actualOptional = cleaningSpecificationsImp.getServiceSpecificationsById(id);

        assertEquals(expectedOptional, actualOptional);
        verify(serviceSpecificationsRepository, times(1)).findById(id);
    }

    /**
     * Test for {@link CleaningSpecificationsImp#getServiceSpecificationsResponseById(Long)}.
     */
    @Test
    void getServiceSpecificationsResponseById_ShouldReturnServiceSpecificationsResponse() {
        // Arrange
        Long id = 1L;
        CleaningSpecifications cleaningSpecifications = new CleaningSpecifications();
        cleaningSpecifications.setId(id);
        ServiceSpecificationsResponse expectedResponse = new ServiceSpecificationsResponse();
        expectedResponse.setId(id);

        when(serviceSpecificationsRepository.findById(id)).thenReturn(Optional.of(cleaningSpecifications));
        when(cleaningSpecificationsMapper.toResponse(cleaningSpecifications)).thenReturn(expectedResponse);

        ServiceSpecificationsResponse actualResponse = cleaningSpecificationsImp.getServiceSpecificationsResponseById(id);

        assertEquals(expectedResponse, actualResponse);
        verify(serviceSpecificationsRepository, times(1)).findById(id);
        verify(cleaningSpecificationsMapper, times(1)).toResponse(cleaningSpecifications);
    }

    /**
     * Test for {@link CleaningSpecificationsImp#getServiceSpecificationsResponseById(Long)}.
     * Should return null when not found.
     */
    @Test
    void getServiceSpecificationsResponseById_ShouldReturnNullWhenNotFound() {

        Long id = 1L;
        when(serviceSpecificationsRepository.findById(id)).thenReturn(Optional.empty());

        ServiceSpecificationsResponse actualResponse = cleaningSpecificationsImp.getServiceSpecificationsResponseById(id);

        assertNull(actualResponse);
        verify(serviceSpecificationsRepository).findById(id);
        verify(cleaningSpecificationsMapper).toResponse(null);

    }

    /**
     * Test for {@link CleaningSpecificationsImp#saveNewServiceSpecifications(ServiceSpecificationsRequest)}.
     */
    @Test
    void saveNewServiceSpecifications_ShouldSaveAndReturnServiceSpecificationsResponse() {

        ServiceSpecificationsRequest request = new ServiceSpecificationsRequest();
        CleaningSpecifications cleaningSpecifications = new CleaningSpecifications();
        cleaningSpecifications.setId(1L);
        ServiceSpecificationsResponse expectedResponse = new ServiceSpecificationsResponse();
        expectedResponse.setId(1L);

        when(cleaningSpecificationsMapper.toEntity(eq(request), any(UploadFile.class))).thenReturn(cleaningSpecifications);
        when(serviceSpecificationsRepository.save(cleaningSpecifications)).thenReturn(cleaningSpecifications);
        when(cleaningSpecificationsMapper.toResponse(cleaningSpecifications)).thenReturn(expectedResponse);

        ServiceSpecificationsResponse actualResponse = cleaningSpecificationsImp.saveNewServiceSpecifications(request);

        assertEquals(expectedResponse, actualResponse);
        verify(cleaningSpecificationsMapper).toEntity(eq(request), any(UploadFile.class));
        verify(serviceSpecificationsRepository).save(cleaningSpecifications);
        verify(cleaningSpecificationsMapper).toResponse(cleaningSpecifications);
    }

    /**
     * Test for {@link CleaningSpecificationsImp#updateServiceSpecifications(Long, ServiceSpecificationsRequest)}.
     */
    @Test
    void updateServiceSpecifications_ShouldUpdateAndReturnServiceSpecificationsResponse() {

        Long id = 1L;
        ServiceSpecificationsRequest request = new ServiceSpecificationsRequest();
        CleaningSpecifications existingSpecifications = new CleaningSpecifications();
        existingSpecifications.setId(id);
        CleaningSpecifications updatedSpecifications = new CleaningSpecifications();
        updatedSpecifications.setId(id);
        ServiceSpecificationsResponse expectedResponse = new ServiceSpecificationsResponse();
        expectedResponse.setId(id);

        when(serviceSpecificationsRepository.findById(id)).thenReturn(Optional.of(existingSpecifications));
        when(cleaningSpecificationsMapper.toEntity(eq(request), any(UploadFile.class))).thenReturn(updatedSpecifications);
        when(serviceSpecificationsRepository.save(updatedSpecifications)).thenReturn(updatedSpecifications);
        when(cleaningSpecificationsMapper.toResponse(updatedSpecifications)).thenReturn(expectedResponse);

        ServiceSpecificationsResponse actualResponse = cleaningSpecificationsImp.updateServiceSpecifications(id, request);

        assertEquals(expectedResponse, actualResponse);
        verify(serviceSpecificationsRepository).findById(id);
        verify(cleaningSpecificationsMapper).toEntity(eq(request), any(UploadFile.class));
        verify(serviceSpecificationsRepository).save(updatedSpecifications);
        verify(cleaningSpecificationsMapper).toResponse(updatedSpecifications);
    }

    /**
     * Test for {@link CleaningSpecificationsImp#updateServiceSpecifications(Long, ServiceSpecificationsRequest)}.
     * Should return null when not found.
     */
    @Test
    void updateServiceSpecifications_ShouldReturnNullWhenNotFound() {
        Long id = 1L;
        ServiceSpecificationsRequest request = new ServiceSpecificationsRequest();
        when(serviceSpecificationsRepository.findById(id)).thenReturn(Optional.empty());

        ServiceSpecificationsResponse actualResponse = cleaningSpecificationsImp.updateServiceSpecifications(id, request);

        assertNull(actualResponse);
        verify(serviceSpecificationsRepository).findById(id);
        verify(cleaningSpecificationsMapper, never()).toEntity(any(), any());
        verify(serviceSpecificationsRepository, never()).save(any());
        verify(cleaningSpecificationsMapper, never()).toResponse(any());
    }

    /**
     * Test for {@link CleaningSpecificationsImp#deleteServiceSpecificationsById(Long)}.
     */
    @Test
    void deleteServiceSpecificationsById_ShouldReturnTrueOnSuccessfulDeletion() {

        Long id = 1L;
        doNothing().when(serviceSpecificationsRepository).deleteById(id);

        boolean result = cleaningSpecificationsImp.deleteServiceSpecificationsById(id);

        assertTrue(result);
        verify(serviceSpecificationsRepository, times(1)).deleteById(id);
    }

    /**
     * Test for {@link CleaningSpecificationsImp#deleteServiceSpecificationsById(Long)}.
     */
    @Test
    void deleteServiceSpecificationsById_ShouldReturnFalseOnDeletionFailure() {

        Long id = 1L;
        doThrow(new RuntimeException("Deletion failed")).when(serviceSpecificationsRepository).deleteById(id);

        boolean result = cleaningSpecificationsImp.deleteServiceSpecificationsById(id);

        assertFalse(result);
        verify(serviceSpecificationsRepository, times(1)).deleteById(id);
    }

    /**
     * Test for {@link CleaningSpecificationsImp#ifServiceSpecificationsMoreThan(int)}.
     */
    @Test
    void ifServiceSpecificationsMoreThan_ShouldReturnTrueIfCountIsGreaterThanThreshold() {

        int threshold = 5;
        when(serviceSpecificationsRepository.count()).thenReturn((long) threshold + 1);

        boolean result = cleaningSpecificationsImp.ifServiceSpecificationsMoreThan(threshold);

        assertTrue(result);
        verify(serviceSpecificationsRepository, times(1)).count();
    }

    /**
     * Test for {@link CleaningSpecificationsImp#ifServiceSpecificationsMoreThan(int)}.
     */
    @Test
    void ifServiceSpecificationsMoreThan_ShouldReturnFalseIfCountIsLessThanOrEqualToThreshold() {

        int threshold = 5;
        when(serviceSpecificationsRepository.count()).thenReturn((long) threshold);

        boolean result = cleaningSpecificationsImp.ifServiceSpecificationsMoreThan(threshold);

        assertFalse(result);
        verify(serviceSpecificationsRepository, times(1)).count();
    }
}
