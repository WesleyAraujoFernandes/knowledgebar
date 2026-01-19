package com.knowledgebar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.knowledgebar.domain.model.product.Category;
import com.knowledgebar.domain.model.product.Product;
import com.knowledgebar.domain.repository.CategoryRepository;
import com.knowledgebar.domain.repository.ProductRepository;
import com.knowledgebar.dto.request.ProductRequestDTO;
import com.knowledgebar.dto.response.ProductResponseDTO;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;

        public ProductResponseDTO create(ProductRequestDTO dto) {
                Category category = categoryRepository.findById(dto.getCategoryId())
                                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

                Product product = Product.builder()
                                .name(dto.getName())
                                .price(dto.getPrice())
                                .stockQuantity(dto.getStockQuantity())
                                .category(category)
                                .build();

                return toResponse(productRepository.save(product));
        }

        public List<ProductResponseDTO> findAll() {
                return productRepository.findAll()
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        public List<ProductResponseDTO> findByCategory(Long categoryId) {
                return productRepository.findByCategoryId(categoryId)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        public ProductResponseDTO findById(Long id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
                return toResponse(product);
        }

        public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

                Category category = categoryRepository.findById(dto.getCategoryId())
                                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

                product.setName(dto.getName());
                product.setPrice(dto.getPrice());
                product.setStockQuantity(dto.getStockQuantity());
                product.setCategory(category);

                return toResponse(productRepository.save(product));
        }

        public void delete(Long id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
                productRepository.delete(product);
        }

        private ProductResponseDTO toResponse(Product product) {
                return ProductResponseDTO.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .price(product.getPrice())
                                .stockQuantity(product.getStockQuantity())
                                .categoryId(product.getCategory().getId())
                                .categoryName(product.getCategory().getName())
                                .build();
        }
}