package com.arturoglezc.ecommerce2.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.arturoglezc.ecommerce2.order.OrderTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order(ORDER_CUSTOMER_NAME, ORDER_DATE, ORDER_STATUS);
    }

    @Test
    void testSaveOrder() {
        Order savedOrder = orderRepository.save(testOrder);
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isNotNull();
    }

    @Test
    void testFindById() {
        orderRepository.save(testOrder);
        Order foundOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertThat(foundOrder).isEqualTo(testOrder);
    }

    @Test
    void testUpdateOrder() {
        Order savedOrder = orderRepository.save(testOrder);

        savedOrder.setCustomerName(ORDER_CUSTOMER_NAME_ALT);
        savedOrder.setOrderDate(ORDER_DATE_ALT);
        savedOrder.setStatus(ORDER_STATUS_ALT);

        Order updatedOrder = orderRepository.save(savedOrder);

        assertThat(updatedOrder.getCustomerName()).isEqualTo(ORDER_CUSTOMER_NAME_ALT);
        assertThat(updatedOrder.getOrderDate()).isEqualTo(ORDER_DATE_ALT);
        assertThat(updatedOrder.getStatus()).isEqualTo(ORDER_STATUS_ALT);
    }

    @Test
    void testDeleteOrder() {
        Order savedOrder = orderRepository.save(testOrder);

        orderRepository.deleteById(savedOrder.getId());

        boolean exists = orderRepository.findById(savedOrder.getId()).isPresent();
        assertThat(exists).isFalse();
    }

    @Test
    void testFindAllOrders() {
        Order secondOrder = new Order(
                ORDER_CUSTOMER_NAME_ALT,
                ORDER_DATE_ALT,
                ORDER_STATUS_ALT);

        orderRepository.saveAll(List.of(testOrder, secondOrder));

        List<Order> orders = orderRepository.findAll();

        assertThat(orders).hasSize(2);
        assertThat(orders).contains(testOrder, secondOrder);
    }
}