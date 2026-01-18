package com.knowledgebar.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.knowledgebar.domain.enums.OrderStatus;

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
public class OrderResponseDTO {

    private Long id;
    private String reference;
    private OrderStatus status;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private BigDecimal total;
    private List<OrderItemResponseDTO> items;
}
