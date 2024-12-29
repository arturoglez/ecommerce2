package com.arturoglezc.ecommerce2.orderitem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Page<OrderItem> findAll(Pageable pageable);
    boolean existsByProductId(Long productId);
    List<OrderItem> findByProductId(Long productId);
    List<OrderItem> findByOrderId(Long orderId);
}
