package com.arturoglezc.ecommerce2.orderitem;

import com.arturoglezc.ecommerce2.helper.JsonPathsTestConstants;
import com.arturoglezc.ecommerce2.order.Order;
import com.arturoglezc.ecommerce2.order.OrderRepository;
import com.arturoglezc.ecommerce2.product.Product;
import com.arturoglezc.ecommerce2.product.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.arturoglezc.ecommerce2.order.OrderTestConstants.*;
import static com.arturoglezc.ecommerce2.orderitem.OrderItemTestConstants.*;
import static com.arturoglezc.ecommerce2.product.ProductTestConstants.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderItemControllerTest {

    private static final String ORDER_ITEM_PATH = "/api/order-items";
    private static final String ORDER_ITEM_BY_ID_PATH = "/api/order-items/{id}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void testCreateOrderItem() throws Exception {
        Order order = orderRepository.save(new Order(ORDER_CUSTOMER_NAME, ORDER_DATE, ORDER_STATUS));
        Product product = productRepository.save(new Product(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK));

        OrderItem newOrderItem = new OrderItem(order, product, ORDER_ITEM_QUANTITY, ORDER_ITEM_UNIT_PRICE);

        mockMvc.perform(post(ORDER_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrderItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JsonPathsTestConstants.ORDER_ITEM_QUANTITY_JSON_PATH).value(ORDER_ITEM_QUANTITY))
                .andExpect(jsonPath(JsonPathsTestConstants.ORDER_ITEM_UNIT_PRICE_JSON_PATH).value(ORDER_ITEM_UNIT_PRICE))
                .andExpect(jsonPath(JsonPathsTestConstants.ORDER_ITEM_ORDER_CUSTOMER_NAME_JSON_PATH).value(order.getCustomerName()))
                .andExpect(jsonPath(JsonPathsTestConstants.ORDER_ITEM_PRODUCT_NAME_JSON_PATH).value(product.getName()));
    }

    @Test
    void testGetOrderItemById() throws Exception {
        Order order = orderRepository.save(new Order(ORDER_CUSTOMER_NAME_ALT, ORDER_DATE_ALT, ORDER_STATUS_ALT));
        Product product = productRepository.save(new Product(PRODUCT_NAME_ALT, PRODUCT_PRICE_ALT, PRODUCT_STOCK_ALT));

        OrderItem savedOrderItem = orderItemRepository.save(new OrderItem(order, product, ORDER_ITEM_QUANTITY, ORDER_ITEM_UNIT_PRICE));

        mockMvc.perform(get(ORDER_ITEM_BY_ID_PATH, savedOrderItem.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JsonPathsTestConstants.ORDER_ITEM_QUANTITY_JSON_PATH).value(ORDER_ITEM_QUANTITY))
                .andExpect(jsonPath(JsonPathsTestConstants.ORDER_ITEM_UNIT_PRICE_JSON_PATH).value(ORDER_ITEM_UNIT_PRICE))
                .andExpect(jsonPath(JsonPathsTestConstants.ORDER_ITEM_ORDER_CUSTOMER_NAME_JSON_PATH).value(order.getCustomerName()))
                .andExpect(jsonPath(JsonPathsTestConstants.ORDER_ITEM_PRODUCT_NAME_JSON_PATH).value(product.getName()));
    }

    @Test
    void testUpdateOrderItem() throws Exception {
        Order order = orderRepository.save(new Order(ORDER_CUSTOMER_NAME_ALT_2, ORDER_DATE_ALT_2, ORDER_STATUS_ALT_2));
        Product product = productRepository.save(new Product(PRODUCT_NAME_ALT_2, PRODUCT_PRICE_ALT_2, PRODUCT_STOCK_ALT_2));

        OrderItem savedOrderItem = orderItemRepository.save(new OrderItem(order, product, ORDER_ITEM_QUANTITY, ORDER_ITEM_UNIT_PRICE));

        OrderItem updatedOrderItem = new OrderItem(order, product, ORDER_ITEM_QUANTITY_ALT, ORDER_ITEM_UNIT_PRICE_ALT);

        mockMvc.perform(put(ORDER_ITEM_BY_ID_PATH, savedOrderItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOrderItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JsonPathsTestConstants.ORDER_ITEM_QUANTITY_JSON_PATH).value(ORDER_ITEM_QUANTITY_ALT))
                .andExpect(jsonPath(JsonPathsTestConstants.ORDER_ITEM_UNIT_PRICE_JSON_PATH).value(ORDER_ITEM_UNIT_PRICE_ALT));
    }

    @Test
    void testDeleteOrderItem() throws Exception {
        Order order = orderRepository.save(new Order(ORDER_CUSTOMER_NAME_ALT_3, ORDER_DATE_ALT_3, ORDER_STATUS_ALT_3));
        Product product = productRepository.save(new Product(PRODUCT_NAME_ALT_3, PRODUCT_PRICE_ALT_3, PRODUCT_STOCK_ALT_3));

        OrderItem savedOrderItem = orderItemRepository.save(new OrderItem(order, product, ORDER_ITEM_QUANTITY, ORDER_ITEM_UNIT_PRICE));

        mockMvc.perform(delete(ORDER_ITEM_BY_ID_PATH, savedOrderItem.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<OrderItem> deletedOrderItem = orderItemRepository.findById(savedOrderItem.getId());
        assertTrue(deletedOrderItem.isEmpty());
    }
}