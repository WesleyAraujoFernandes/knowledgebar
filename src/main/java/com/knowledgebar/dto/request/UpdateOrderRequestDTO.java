package com.knowledgebar.dto.request;

import java.math.BigDecimal;

import com.knowledgebar.domain.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequestDTO {
    private OrderStatus status;
    private BigDecimal paymentAmount;
    private String reference;
}
