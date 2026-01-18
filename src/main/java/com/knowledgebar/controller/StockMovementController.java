package com.knowledgebar.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgebar.dto.request.StockMovementRequestDTO;
import com.knowledgebar.dto.response.StockMovementResponseDTO;
import com.knowledgebar.service.StockMovementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @PostMapping
    public ResponseEntity<StockMovementResponseDTO> create(
            @Valid @RequestBody StockMovementRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(stockMovementService.create(dto));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockMovementResponseDTO>> findByProduct(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(stockMovementService.findByProduct(productId));
    }
}