package com.knowledgebar.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.knowledgebar.domain.enums.OrderStatus;
import com.knowledgebar.dto.response.OrderItemResponseDTO;

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
public class CreateOrderRequestDTO {

    private String reference;
    private OrderStatus status;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private BigDecimal total;
    private List<OrderItemResponseDTO> items;
    
}
