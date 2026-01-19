package com.knowledgebar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgebar.dto.request.UpdateItemRequestDTO;
import com.knowledgebar.dto.request.UpdateOrderRequestDTO;
import com.knowledgebar.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PostMapping
    public ResponseEntity<Long> create() {
        Long orderId = orderService.createOrder();
        return ResponseEntity.ok(orderId);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Void> update(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderRequestDTO request) {
        orderService.updateOrder(orderId, request.getStatus(), request.getPaymentAmount(), request.getReference());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<Void> addItem(
            @PathVariable Long orderId,
            @RequestBody UpdateItemRequestDTO request) {

        orderService.addItem(orderId, request.getProductId(), request.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{orderId}/close")
    public ResponseEntity<Void> close(@PathVariable Long orderId) {
        orderService.closeOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
