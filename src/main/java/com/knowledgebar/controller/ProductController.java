package com.knowledgebar.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgebar.dto.request.ProductRequestDTO;
import com.knowledgebar.dto.response.ProductResponseDTO;
import com.knowledgebar.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(
            @Valid @RequestBody ProductRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> findByCategory(
            @PathVariable Long categoryId
    ) {
        return ResponseEntity.ok(productService.findByCategory(categoryId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO dto
    ) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
