package com.arturoglezc.ecommerce2.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static com.arturoglezc.ecommerce2.helper.JsonPathsTestConstants.*;
import static com.arturoglezc.ecommerce2.order.OrderTestConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerTest {

    private static final String ORDER_ID_PATH = "/api/orders/{id}";
    private static final String GET_ALL_ORDERS_PAGING_PATH = "/api/orders?page=0&size=2";
    private static final String CREATE_ORDER_PATH = "/api/orders";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm"));
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
    }

    @Test
    void testGetOrderById() throws Exception {
        Order order = orderRepository.save(
                new Order(ORDER_CUSTOMER_NAME, ORDER_DATE, ORDER_STATUS));


        mockMvc.perform(get(ORDER_ID_PATH, order.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ORDER_CUSTOMER_NAME_JSON_PATH).value(ORDER_CUSTOMER_NAME))
                .andExpect(jsonPath(ORDER_DATE_JSON_PATH).value(ORDER_DATE.toString()))
                .andExpect(jsonPath(ORDER_STATUS_JSON_PATH).value(ORDER_STATUS.toString()));
    }

    @Test
    void testGetAllOrders_ShouldReturnPaginatedResults() throws Exception {
        orderRepository.saveAll(Arrays.asList(
                new Order(ORDER_CUSTOMER_NAME, ORDER_DATE, ORDER_STATUS),
                new Order(ORDER_CUSTOMER_NAME_ALT, ORDER_DATE_ALT, ORDER_STATUS_ALT),
                new Order(ORDER_CUSTOMER_NAME_ALT_2, ORDER_DATE_ALT_2, ORDER_STATUS_ALT_2),
                new Order(ORDER_CUSTOMER_NAME_ALT_3, ORDER_DATE_ALT_3, ORDER_STATUS_ALT_3)
        ));

        mockMvc.perform(get(GET_ALL_ORDERS_PAGING_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(CONTENT_JSON_PATH, hasSize(2)))
                .andExpect(jsonPath(ORDER_CUSTOMER_NAME_FIRST_ELEMENT_JSON_PATH).value(ORDER_CUSTOMER_NAME))
                .andExpect(jsonPath(ORDER_CUSTOMER_NAME_SECOND_ELEMENT_JSON_PATH).value(ORDER_CUSTOMER_NAME_ALT))
                .andExpect(jsonPath(TOTAL_ELEMENTS_JSON_PATH).value(4))
                .andExpect(jsonPath(TOTAL_PAGES_JSON_PATH).value(2))
                .andExpect(jsonPath(PAGE_NUMBER_JSON_PATH).value(0));
    }

    @Test
    void testCreateOrder() throws Exception {
        Order newOrder = new Order(ORDER_CUSTOMER_NAME, ORDER_DATE, ORDER_STATUS);

        mockMvc.perform(post(CREATE_ORDER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(ORDER_CUSTOMER_NAME_JSON_PATH).value(ORDER_CUSTOMER_NAME))
                .andExpect(jsonPath(ORDER_DATE_JSON_PATH).value(ORDER_DATE.toString()))
                .andExpect(jsonPath(ORDER_STATUS_JSON_PATH).value(ORDER_STATUS.toString()));
    }

    @Test
    void testUpdateOrder() throws Exception {
        Order existingOrder = orderRepository.save(new Order(ORDER_CUSTOMER_NAME, ORDER_DATE, ORDER_STATUS));

        Order updatedOrder = new Order(ORDER_CUSTOMER_NAME_ALT, ORDER_DATE_ALT, ORDER_STATUS_ALT);

        String updatedOrderJson = objectMapper.writeValueAsString(updatedOrder);

        mockMvc.perform(put(ORDER_ID_PATH, existingOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedOrderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ORDER_CUSTOMER_NAME_JSON_PATH).value(ORDER_CUSTOMER_NAME_ALT))
                .andExpect(jsonPath(ORDER_DATE_JSON_PATH).value(ORDER_DATE_ALT.toString()))
                .andExpect(jsonPath(ORDER_STATUS_JSON_PATH).value(ORDER_STATUS_ALT.toString()));
    }

    @Test
    void testDeleteOrderById() throws Exception {
        Order order = orderRepository.save(new Order(ORDER_CUSTOMER_NAME, ORDER_DATE, ORDER_STATUS));

        mockMvc.perform(delete(ORDER_ID_PATH, order.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<Order> deletedOrder = orderRepository.findById(order.getId());
        assertTrue(deletedOrder.isEmpty());
    }
}