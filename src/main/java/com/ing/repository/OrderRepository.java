package com.ing.repository;

import com.ing.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdAndCreateDateBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByCustomerId(Long customerId);

    @Query("SELECT DISTINCT o.asset.assetName FROM Order o WHERE o.customer.id = :customerId")
    List<String> findDistinctAssetNamesByCustomerId(Long customerId);
}