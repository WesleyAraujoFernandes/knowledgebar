package com.knowledgebar.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.knowledgebar.domain.enums.OrderStatus;
import com.knowledgebar.domain.enums.StockMovementType;
import com.knowledgebar.domain.model.Order;
import com.knowledgebar.domain.model.OrderItem;
import com.knowledgebar.domain.model.Product;
import com.knowledgebar.domain.model.StockMovement;
import com.knowledgebar.domain.repository.OrderRepository;
import com.knowledgebar.domain.repository.ProductRepository;
import com.knowledgebar.domain.repository.StockMovementRepository;
import com.knowledgebar.dto.response.OrderItemResponseDTO;
import com.knowledgebar.dto.response.OrderResponseDTO;
import com.knowledgebar.exception.BusinessException;
import com.knowledgebar.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            StockMovementRepository stockMovementRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Transactional
    public Long openOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.OPEN);
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);
        return order.getId();
    }

@Transactional
public OrderResponseDTO getOrderById(Long orderId) {

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada"));

    List<OrderItemResponseDTO> items = order.getItems().stream()
            .map(item -> {
                BigDecimal total = item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                return new OrderItemResponseDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getProduct().getPrice(),
                        total
                );
            })
            .toList();

    BigDecimal total = items.stream()
            .map(OrderItemResponseDTO::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new OrderResponseDTO(
            order.getId(),
            order.getStatus(),
            order.getCreatedAt(),
            order.getCanceledAt(),
            order.getReference(),
            total,
            items
    );
}


    public void addItem(Long orderId, Long productId, Integer quantity) {
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        if (product.getStockQuantity() < quantity) {
            throw new BusinessException("Estoque insuficiente para o produto: " + product.getName());
        }

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new BusinessException("Não é possível adicionar itens a uma comanda que não está aberta");
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        order.getItems().add(orderItem);
        orderRepository.save(order);

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
        
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setType(StockMovementType.OUT);
        movement.setQuantity(quantity);
        movement.setReason("Saída por comanda " + order.getId());
        movement.setCreatedAt(LocalDateTime.now());
        stockMovementRepository.save(movement);
    }

    @Transactional
    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada"));
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new BusinessException("Comanda não possui itens para cancelamento");
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new BusinessException("Comanda já está cancelada");
        }

        if (order.getStatus() == OrderStatus.CLOSED) {
            throw new BusinessException("Comanda já foi finalizada e não pode ser cancelada");
        }

        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();
            Integer quantity = item.getQuantity();

            product.setStockQuantity(product.getStockQuantity() + quantity);
            productRepository.save(product);

            StockMovement movement = new StockMovement();
            movement.setProduct(product);
            movement.setType(StockMovementType.CANCELED_ORDER);
            movement.setQuantity(quantity);
            movement.setReason("Estorno de cancelamento da comanda " + order.getId());
            movement.setCreatedAt(LocalDateTime.now());

            stockMovementRepository.save(movement);
        }

        order.setStatus(OrderStatus.CANCELED);
        order.setCanceledAt(LocalDateTime.now());

        orderRepository.save(order);
    }

    @Transactional
    public void closeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada"));

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new BusinessException("Comanda não possui itens para finalização");
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new BusinessException("Comanda está cancelada e não pode ser finalizada");
        }

        order.setStatus(OrderStatus.CLOSED);
        orderRepository.save(order);
    }

    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> {
                    List<OrderItemResponseDTO> items = order.getItems().stream()
                            .map(item -> {
                                BigDecimal total = item.getProduct().getPrice()
                                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                                return new OrderItemResponseDTO(
                                        item.getProduct().getId(),
                                        item.getProduct().getName(),
                                        item.getQuantity(),
                                        item.getProduct().getPrice(),
                                        total
                                );
                            })
                            .toList();

                    BigDecimal total = items.stream()
                            .map(OrderItemResponseDTO::getTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new OrderResponseDTO(
                            order.getId(),
                            order.getStatus(),
                            order.getCreatedAt(),
                            order.getCanceledAt(),
                            order.getReference(),
                            total,
                            items
                    );
                })
                .toList();
    }
}