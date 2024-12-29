package com.arturoglezc.ecommerce2.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.arturoglezc.ecommerce2.order.OrderTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceTest {

    private static final String INVALID_ORDER_DATA_MSG = "Invalid order data";

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order(ORDER_CUSTOMER_NAME, ORDER_DATE, ORDER_STATUS);
    }

    @Test
    void testGetOrderById_ShouldReturnOrder() {
        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        Order foundOrder = orderService.getOrderById(testOrder.getId());

        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getCustomerName()).isEqualTo(ORDER_CUSTOMER_NAME);
        assertThat(foundOrder.getOrderDate()).isEqualTo(ORDER_DATE);
        assertThat(foundOrder.getStatus()).isEqualTo(ORDER_STATUS);
        verify(orderRepository, times(1)).findById(testOrder.getId());
    }

    @Test
    void testGetOrderById_ShouldThrowExceptionIfNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void testGetAllOrders_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Order> orders = Arrays.asList(
                new Order(ORDER_CUSTOMER_NAME, ORDER_DATE, ORDER_STATUS),
                new Order(ORDER_CUSTOMER_NAME_ALT, ORDER_DATE_ALT, ORDER_STATUS_ALT)
        );

        Page<Order> page = new PageImpl<>(orders, pageable, 2);

        when(orderRepository.findAll(pageable)).thenReturn(page);

        Page<Order> result = orderService.getAllOrders(pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().getFirst().getCustomerName()).isEqualTo(ORDER_CUSTOMER_NAME);
        verify(orderRepository, times(1)).findAll(pageable);
    }

    @Test
    void testCreateOrder_ShouldSaveAndReturnOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order savedOrder = orderService.createOrder(testOrder);

        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getCustomerName()).isEqualTo(ORDER_CUSTOMER_NAME);
        assertThat(savedOrder.getOrderDate()).isEqualTo(ORDER_DATE);
        assertThat(savedOrder.getStatus()).isEqualTo(ORDER_STATUS);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCreateOrder_ShouldThrowExceptionForInvalidOrder() {
        Order invalidOrder = new Order(null, null, null);

        when(orderRepository.save(any(Order.class)))
                .thenThrow(new IllegalArgumentException(INVALID_ORDER_DATA_MSG));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(invalidOrder);
        });

        assertThat(exception.getMessage()).isEqualTo(INVALID_ORDER_DATA_MSG);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateOrder_ShouldUpdateAndReturnOrder() {
        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        Order updatedOrder = new Order(ORDER_CUSTOMER_NAME_ALT, ORDER_DATE_ALT, ORDER_STATUS_ALT);
        updatedOrder.setId(testOrder.getId());

        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        Order result = orderService.updateOrder(testOrder.getId(), updatedOrder);

        assertThat(result.getCustomerName()).isEqualTo(ORDER_CUSTOMER_NAME_ALT);
        assertThat(result.getOrderDate()).isEqualTo(ORDER_DATE_ALT);
        assertThat(result.getStatus()).isEqualTo(ORDER_STATUS_ALT);

        verify(orderRepository, times(1)).findById(testOrder.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateOrder_ShouldThrowExceptionIfNotFound() {
        Long invalidId = 999L;
        when(orderRepository.findById(invalidId)).thenReturn(Optional.empty());

        Order updatedOrder = new Order(ORDER_CUSTOMER_NAME_ALT, ORDER_DATE_ALT, ORDER_STATUS_ALT);

        assertThrows(RuntimeException.class, () -> orderService.updateOrder(invalidId, updatedOrder));

        verify(orderRepository, times(1)).findById(invalidId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testDeleteOrder_ShouldDeleteOrder() {
        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        orderService.deleteOrder(testOrder.getId());

        verify(orderRepository, times(1)).findById(testOrder.getId());
        verify(orderRepository, times(1)).deleteById(testOrder.getId());
    }

    @Test
    void testDeleteOrder_ShouldThrowExceptionIfNotFound() {
        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            orderService.deleteOrder(testOrder.getId());
        });
    }
}