package com.knowledgebar.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.knowledgebar.domain.enums.StockMovementType;
import com.knowledgebar.domain.model.product.Product;
import com.knowledgebar.domain.model.stock.StockMovement;
import com.knowledgebar.domain.repository.ProductRepository;
import com.knowledgebar.domain.repository.StockMovementRepository;
import com.knowledgebar.dto.request.StockMovementRequestDTO;
import com.knowledgebar.dto.response.StockMovementResponseDTO;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;

    @Transactional
    public StockMovementResponseDTO create(StockMovementRequestDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        int newStock = calculateStock(
                product.getStockQuantity(),
                dto.getQuantity(),
                dto.getType());

        product.setStockQuantity(newStock);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .type(dto.getType())
                .quantity(dto.getQuantity())
                .reason(dto.getReason())
                .createdAt(LocalDateTime.now())
                .build();

        productRepository.save(product);
        return toResponse(stockMovementRepository.save(movement));
    }

    public List<StockMovementResponseDTO> findByProduct(Long productId) {
        return stockMovementRepository.findByProductId(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private int calculateStock(int current, int quantity, StockMovementType type) {
        return switch (type) {
            case IN -> current + quantity;
            case OUT -> {
                if (current < quantity) {
                    throw new IllegalArgumentException("Insufficient stock");
                }
                yield current - quantity;
            }
            case ADJUST -> quantity;
            case CANCELED_ORDER -> throw new UnsupportedOperationException("Unimplemented case: " + type);
            default -> throw new IllegalArgumentException("Unexpected value: " + type);
        };
    }

    private StockMovementResponseDTO toResponse(StockMovement movement) {
        return StockMovementResponseDTO.builder()
                .id(movement.getId())
                .type(movement.getType())
                .quantity(movement.getQuantity())
                .reason(movement.getReason())
                .createdAt(movement.getCreatedAt())
                .productId(movement.getProduct().getId())
                .productName(movement.getProduct().getName())
                .build();
    }
}