package com.example.cleanorarest.repository;

import com.example.cleanorarest.entity.Workday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.Optional;

public interface WorkdayRepository extends JpaRepository<Workday, Long>, JpaSpecificationExecutor<Workday> {
    Optional<Workday> findByDate(LocalDate currentDate);
}