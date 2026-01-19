package com.knowledgebar.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knowledgebar.domain.enums.OrderStatus;
import com.knowledgebar.domain.model.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(OrderStatus status);
}