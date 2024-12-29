package com.arturoglezc.ecommerce2.orderitem;

import com.arturoglezc.ecommerce2.product.Product;
import com.arturoglezc.ecommerce2.product.ProductRepository;
import com.arturoglezc.ecommerce2.order.Order;
import com.arturoglezc.ecommerce2.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static com.arturoglezc.ecommerce2.order.OrderTestConstants.*;
import static com.arturoglezc.ecommerce2.orderitem.OrderItemTestConstants.*;
import static com.arturoglezc.ecommerce2.product.ProductTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private Order testOrder;
    private Product testProduct;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        testProduct = new Product(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK);
        productRepository.save(testProduct);

        testOrder = new Order(ORDER_CUSTOMER_NAME, ORDER_DATE, ORDER_STATUS);
        orderRepository.save(testOrder);

        testOrderItem = new OrderItem(testOrder, testProduct, ORDER_ITEM_QUANTITY, ORDER_ITEM_UNIT_PRICE);
    }

    @Test
    void testSaveOrderItem() {
        OrderItem savedOrderItem = orderItemRepository.save(testOrderItem);
        assertThat(savedOrderItem).isNotNull();
        assertThat(savedOrderItem.getId()).isNotNull();
        assertThat(savedOrderItem.getOrder()).isEqualTo(testOrder);
        assertThat(savedOrderItem.getProduct()).isEqualTo(testProduct);
    }

    @Test
    void testFindById() {
        orderItemRepository.save(testOrderItem);
        OrderItem foundOrderItem = orderItemRepository.findById(testOrderItem.getId()).orElseThrow();
        assertThat(foundOrderItem).isEqualTo(testOrderItem);
    }

    @Test
    void testFindByProductId() {
        orderItemRepository.save(testOrderItem);
        List<OrderItem> foundOrderItems = orderItemRepository.findByProductId(testProduct.getId());
        assertThat(foundOrderItems).hasSize(1);
        assertThat(foundOrderItems.getFirst().getProduct()).isEqualTo(testProduct);
    }

    @Test
    void testUpdateOrderItem() {
        orderItemRepository.save(testOrderItem);

        testOrderItem.setQuantity(ORDER_ITEM_QUANTITY_ALT);

        OrderItem updatedOrderItem = orderItemRepository.save(testOrderItem);

        assertThat(updatedOrderItem.getQuantity()).isEqualTo(ORDER_ITEM_QUANTITY_ALT);
    }

    @Test
    void testDeleteOrderItem() {
        orderItemRepository.save(testOrderItem);

        orderItemRepository.deleteById(testOrderItem.getId());

        Optional<OrderItem> deletedOrderItem = orderItemRepository.findById(testOrderItem.getId());
        assertThat(deletedOrderItem).isEmpty();
    }

    @Test
    void testFindAllOrderItems() {
        OrderItem secondOrderItem = new OrderItem(testOrder, testProduct, ORDER_ITEM_QUANTITY, ORDER_ITEM_UNIT_PRICE);
        orderItemRepository.saveAll(List.of(testOrderItem, secondOrderItem));

        List<OrderItem> orderItems = orderItemRepository.findAll();
        assertThat(orderItems).hasSize(2);
        assertThat(orderItems).contains(testOrderItem, secondOrderItem);
    }

    @Test
    void testFindByOrderId() {
        orderItemRepository.save(testOrderItem);

        List<OrderItem> foundOrderItems = orderItemRepository.findByOrderId(testOrder.getId());
        assertThat(foundOrderItems).hasSize(1);
        assertThat(foundOrderItems.getFirst().getOrder()).isEqualTo(testOrder);
    }
}