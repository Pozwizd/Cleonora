package com.example.cleanorarest.repository;

import com.example.cleanorarest.entity.OrderCleaning;
import com.example.cleanorarest.entity.TimeSlot;
import com.example.cleanorarest.entity.Workday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long>, JpaSpecificationExecutor<TimeSlot> {
    Optional<TimeSlot> findFirstByDateAndStartTimeLessThanAndEndTimeGreaterThan(LocalDate date, LocalTime startTime, LocalTime endTime);
}