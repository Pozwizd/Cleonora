package com.example.cleanorarest.service.Imp;


import com.example.cleanorarest.entity.Cleaning;
import com.example.cleanorarest.mapper.CleaningMapper;
import com.example.cleanorarest.model.cleaning.CleaningResponse;
import com.example.cleanorarest.repository.CleaningRepository;
import com.example.cleanorarest.service.CleaningService;
import com.example.cleanorarest.specification.CleaningSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CleaningServiceImp implements CleaningService {


    private final CleaningRepository cleaningRepository;
    private final CleaningMapper cleaningMapper;

    public CleaningServiceImp(CleaningRepository cleaningRepository, CleaningMapper cleaningMapper) {
        this.cleaningRepository = cleaningRepository;
        this.cleaningMapper = cleaningMapper;
    }


    @Override
    public Page<CleaningResponse> getPageAllCleaningByCategoryId(int page, Integer size, Long categoryId) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("id")));
        return cleaningMapper.toResponsePage(cleaningRepository.findAll(CleaningSpecification.byCategoryId(categoryId), pageRequest));
    }

    @Override
    public Cleaning getServiceById(Long id) {
        return cleaningRepository.findById(id).orElse(null);
    }

    @Override
    public CleaningResponse getServiceResponseById(Long id) {
        return cleaningMapper.toResponse(cleaningRepository.findById(id).orElse(null));
    }

    @Override
    public boolean ifServiceMoreThan(int i) {
        return cleaningRepository.count() > i;
    }

    @Override
    public Cleaning save(Cleaning cleaning) {
        return cleaningRepository.save(cleaning);
    }


}
