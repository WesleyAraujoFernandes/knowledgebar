package com.knowledgebar.service;

import java.time.LocalDateTime;

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
    public Long openOrder() {

        Order order = new Order();
        order.setStatus(OrderStatus.OPEN);

        orderRepository.save(order);

        return order.getId();
    }

}