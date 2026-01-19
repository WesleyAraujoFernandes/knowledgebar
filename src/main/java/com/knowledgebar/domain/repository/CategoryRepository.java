package com.knowledgebar.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knowledgebar.domain.model.product.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);
}
