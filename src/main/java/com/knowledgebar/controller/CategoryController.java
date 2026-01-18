package com.knowledgebar.controller;

import com.knowledgebar.service.CategoryService;
import com.knowledgebar.dto.request.CategoryRequestDTO;
import com.knowledgebar.dto.response.CategoryResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(
            @Valid @RequestBody CategoryRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO dto
    ) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}