package com.knowledgebar.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class CategoryRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must have at most 100 characters")
    private String name;

    @Size(max = 255, message = "Description must have at most 255 characters")
    private String description;
}