package com.knowledgebar.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knowledgebar.domain.model.stock.StockMovement;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProductId(Long productId);
}
