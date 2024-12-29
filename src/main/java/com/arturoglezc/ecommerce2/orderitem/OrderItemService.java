package com.arturoglezc.ecommerce2.orderitem;

import com.arturoglezc.ecommerce2.product.Product;
import com.arturoglezc.ecommerce2.product.ProductRepository;
import com.arturoglezc.ecommerce2.order.Order;
import com.arturoglezc.ecommerce2.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class OrderItemService {

    private static final String ORDER_ITEM_NOT_FOUND = "OrderItem not found";
    private static final String ORDER_NOT_FOUND = "Order not found";
    private static final String PRODUCT_NOT_FOUND = "Product not found";
    private static final String PRODUCT_ASSOCIATED_CANNOT_BE_DELETED = "Product is associated with existing order items and cannot be deleted.";
    private static final String NOT_ENOUGH_STOCK = "Not enough stock";

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Page<OrderItem> getAllOrderItems(Pageable pageable) {
        return orderItemRepository.findAll(pageable);
    }

    public OrderItem getOrderItemById(Long id) {
        return findOrderItemById(id);
    }

    public OrderItem createOrderItem(OrderItem orderItem) {
        Product product = findProductById(orderItem.getProduct().getId());

        if (product.getStock() < orderItem.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NOT_ENOUGH_STOCK);
        }

        product.setStock(product.getStock() - orderItem.getQuantity());
        productRepository.save(product);

        Order order = findOrderById(orderItem.getOrder().getId());

        orderItem.setProduct(product);
        orderItem.setOrder(order);

        return orderItemRepository.save(orderItem);
    }

    public OrderItem updateOrderItem(Long id, OrderItem orderItem) {
        OrderItem existingOrderItem = findOrderItemById(id);

        Product product = findProductById(orderItem.getProduct().getId());

        int stockAdjustment = orderItem.getQuantity() - existingOrderItem.getQuantity();
        if (product.getStock() < stockAdjustment) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NOT_ENOUGH_STOCK);
        }
        product.setStock(product.getStock() - stockAdjustment);
        productRepository.save(product);

        Order order = findOrderById(orderItem.getOrder().getId());

        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setId(id);

        return orderItemRepository.save(orderItem);
    }

    public void deleteOrderItem(Long id) {
        OrderItem orderItem = findOrderItemById(id);

        Product product = findProductById(orderItem.getProduct().getId());
        product.setStock(product.getStock() + orderItem.getQuantity());
        productRepository.save(product);

        orderItemRepository.deleteById(id);
    }

    public void deleteOrder(Long id) {
        Order order = findOrderById(id);
        orderRepository.deleteById(id);
    }

    public void deleteProduct(Long id) {
        Product product = findProductById(id);

        if (orderItemRepository.existsByProductId(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PRODUCT_ASSOCIATED_CANNOT_BE_DELETED);
        }

        productRepository.deleteById(id);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND));
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ORDER_NOT_FOUND));
    }

    private OrderItem findOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ORDER_ITEM_NOT_FOUND));
    }
}