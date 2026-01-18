package com.knowledgebar.dto.response;

import java.time.LocalDateTime;

import com.knowledgebar.domain.enums.StockMovementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementResponseDTO {

    private Long id;
    private StockMovementType type;
    private Integer quantity;
    private String reason;
    private LocalDateTime createdAt;

    private Long productId;
    private String productName;
}