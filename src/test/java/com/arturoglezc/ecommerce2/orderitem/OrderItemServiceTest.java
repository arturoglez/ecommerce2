package com.arturoglezc.ecommerce2.orderitem;

import com.arturoglezc.ecommerce2.order.Order;
import com.arturoglezc.ecommerce2.order.OrderRepository;
import com.arturoglezc.ecommerce2.product.Product;
import com.arturoglezc.ecommerce2.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.arturoglezc.ecommerce2.order.OrderTestConstants.*;
import static com.arturoglezc.ecommerce2.orderitem.OrderItemTestConstants.*;
import static com.arturoglezc.ecommerce2.product.ProductTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    private OrderItem testOrderItem;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testProduct = new Product(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK);

        testOrder = new Order(ORDER_CUSTOMER_NAME, ORDER_DATE, ORDER_STATUS);

        testOrderItem = new OrderItem(testOrder, testProduct, ORDER_ITEM_QUANTITY, ORDER_ITEM_UNIT_PRICE);
    }

    @Test
    void testGetOrderItemById_ShouldReturnOrderItem() {
        when(orderItemRepository.findById(testOrderItem.getId())).thenReturn(Optional.of(testOrderItem));

        OrderItem foundOrderItem = orderItemService.getOrderItemById(testOrderItem.getId());

        assertThat(foundOrderItem).isNotNull();
        assertThat(foundOrderItem.getQuantity()).isEqualTo(ORDER_ITEM_QUANTITY);
        assertThat(foundOrderItem.getProduct().getName()).isEqualTo(PRODUCT_NAME);
        verify(orderItemRepository, times(1)).findById(testOrderItem.getId());
    }

    @Test
    void testGetOrderItemById_ShouldThrowExceptionIfNotFound() {
        when(orderItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> orderItemService.getOrderItemById(999L));
    }

    @Test
    void testCreateOrderItem_ShouldSaveAndReturnOrderItem() {
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));
        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        OrderItem savedOrderItem = orderItemService.createOrderItem(testOrderItem);

        assertThat(savedOrderItem).isNotNull();
        assertThat(savedOrderItem.getQuantity()).isEqualTo(ORDER_ITEM_QUANTITY);
        assertThat(testProduct.getStock()).isEqualTo(PRODUCT_STOCK - ORDER_ITEM_QUANTITY);
        verify(productRepository, times(1)).save(testProduct);
        verify(orderItemRepository, times(1)).save(testOrderItem);
    }

    @Test
    void testCreateOrderItem_ShouldThrowExceptionIfNotEnoughStock() {
        testOrderItem.setQuantity(200);
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        assertThrows(ResponseStatusException.class, () -> orderItemService.createOrderItem(testOrderItem));
    }

    @Test
    void testUpdateOrderItem_ShouldUpdateAndReturnOrderItem() {
        when(orderItemRepository.findById(testOrderItem.getId())).thenReturn(Optional.of(testOrderItem));
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));
        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        testOrderItem.setQuantity(20);
        OrderItem updatedOrderItem = orderItemService.updateOrderItem(testOrderItem.getId(), testOrderItem);

        assertThat(updatedOrderItem.getQuantity()).isEqualTo(20);
        verify(orderItemRepository, times(1)).save(testOrderItem);
    }

    @Test
    void testDeleteOrderItem_ShouldRestoreStockAndDeleteOrderItem() {
        when(orderItemRepository.findById(testOrderItem.getId())).thenReturn(Optional.of(testOrderItem));
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        orderItemService.deleteOrderItem(testOrderItem.getId());

        assertThat(testProduct.getStock()).isEqualTo(PRODUCT_STOCK + ORDER_ITEM_QUANTITY);
        verify(productRepository, times(1)).save(testProduct);
        verify(orderItemRepository, times(1)).deleteById(testOrderItem.getId());
    }

    @Test
    void testDeleteOrderItem_ShouldThrowExceptionIfNotFound() {
        when(orderItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> orderItemService.deleteOrderItem(999L));
    }
}