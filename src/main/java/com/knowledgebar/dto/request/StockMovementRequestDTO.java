package com.knowledgebar.dto.request;

import com.knowledgebar.domain.enums.StockMovementType;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementRequestDTO {

    @NotNull
    private Long productId;

    @NotNull
    private StockMovementType type;

    @NotNull
    @Min(1)
    private Integer quantity;

    @Size(max = 255)
    private String reason;
}
