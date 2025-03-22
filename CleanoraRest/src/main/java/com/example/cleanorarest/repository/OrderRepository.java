package com.example.cleanorarest.repository;

import com.example.cleanorarest.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query("SELECT COUNT(o.id) FROM Order o WHERE o.status = 'COMPLETED'")
    Integer countCompletedOrders();

}


