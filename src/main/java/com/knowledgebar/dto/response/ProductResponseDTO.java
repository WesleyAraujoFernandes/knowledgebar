package com.knowledgebar.dto.response;

import java.math.BigDecimal;

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
public class ProductResponseDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;

    private Long categoryId;
    private String categoryName;
}