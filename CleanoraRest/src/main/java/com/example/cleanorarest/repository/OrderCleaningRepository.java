package com.example.cleanorarest.repository;

import com.example.cleanorarest.entity.OrderCleaning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderCleaningRepository extends JpaRepository<OrderCleaning, Long>, JpaSpecificationExecutor<OrderCleaning> {

}