package com.example.cleanorarest.service;

import com.example.cleanorarest.entity.Cleaning;
import com.example.cleanorarest.model.cleaning.CleaningResponse;
import org.springframework.data.domain.Page;

public interface CleaningService {

    Page<CleaningResponse> getPageAllCleaningByCategoryId(int page, Integer size, Long categoryId);

    Cleaning getServiceById(Long id);

    CleaningResponse getServiceResponseById(Long id);

    boolean ifServiceMoreThan(int i);

    Cleaning save(Cleaning cleaning);
}

