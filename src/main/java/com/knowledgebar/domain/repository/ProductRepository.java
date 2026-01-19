package com.knowledgebar.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knowledgebar.domain.model.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);
}