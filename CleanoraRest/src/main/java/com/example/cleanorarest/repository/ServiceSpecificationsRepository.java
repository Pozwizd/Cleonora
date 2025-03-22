package com.example.cleanorarest.repository;

import com.example.cleanorarest.entity.CleaningSpecifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceSpecificationsRepository extends JpaRepository<CleaningSpecifications, Long>, JpaSpecificationExecutor<CleaningSpecifications> {
}