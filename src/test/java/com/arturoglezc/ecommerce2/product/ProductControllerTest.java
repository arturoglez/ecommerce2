package com.arturoglezc.ecommerce2.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
import static com.arturoglezc.ecommerce2.product.ProductTestConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerTest {

    private static final String PRODUCT_ID_PATH = "/api/products/{id}";
    private static final String GET_ALL_PRODUCTS_PAGING_PATH = "/api/products?page=0&size=2";
    private static final String CREATE_PRODUCT_PATH = "/api/products";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void testGetProductById() throws Exception {
        Product product = productRepository.save(
                new Product(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK));

        mockMvc.perform(get(PRODUCT_ID_PATH, product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(PRODUCT_NAME_JSON_PATH).value(PRODUCT_NAME))
                .andExpect(jsonPath(PRODUCT_PRICE_JSON_PATH).value(PRODUCT_PRICE))
                .andExpect(jsonPath(PRODUCT_STOCK_JSON_PATH).value(PRODUCT_STOCK));
    }

    @Test
    void testGetAllProducts_ShouldReturnPaginatedResults() throws Exception {
        productRepository.saveAll(Arrays.asList(
                new Product(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK),
                new Product(PRODUCT_NAME_ALT, PRODUCT_PRICE_ALT, PRODUCT_STOCK_ALT),
                new Product(PRODUCT_NAME_ALT_2, PRODUCT_PRICE_ALT_2, PRODUCT_STOCK_ALT_2),
                new Product(PRODUCT_NAME_ALT_3, PRODUCT_PRICE_ALT_3, PRODUCT_STOCK_ALT_3)
        ));

        mockMvc.perform(get(GET_ALL_PRODUCTS_PAGING_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(CONTENT_JSON_PATH, hasSize(2)))
                .andExpect(jsonPath(PRODUCT_NAME_FIRST_ELEMENT_JSON_PATH).value(PRODUCT_NAME))
                .andExpect(jsonPath(PRODUCT_NAME_SECOND_ELEMENT_JSON_PATH).value(PRODUCT_NAME_ALT))
                .andExpect(jsonPath(TOTAL_ELEMENTS_JSON_PATH).value(4))
                .andExpect(jsonPath(TOTAL_PAGES_JSON_PATH).value(2))
                .andExpect(jsonPath(PAGE_NUMBER_JSON_PATH).value(0));
    }

    @Test
    void testCreateProduct() throws Exception {
        Product newProduct = new Product(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK);

        mockMvc.perform(post(CREATE_PRODUCT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(PRODUCT_NAME_JSON_PATH).value(PRODUCT_NAME))
                .andExpect(jsonPath(PRODUCT_PRICE_JSON_PATH).value(PRODUCT_PRICE))
                .andExpect(jsonPath(PRODUCT_STOCK_JSON_PATH).value(PRODUCT_STOCK));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product existingProduct = productRepository.save(new Product(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK));

        Product updatedProduct = new Product(PRODUCT_NAME_ALT, PRODUCT_PRICE_ALT, PRODUCT_STOCK_ALT);

        String updatedProductJson = objectMapper.writeValueAsString(updatedProduct);

        mockMvc.perform(put(PRODUCT_ID_PATH, existingProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedProductJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath(PRODUCT_NAME_JSON_PATH).value(PRODUCT_NAME_ALT))
                .andExpect(jsonPath(PRODUCT_PRICE_JSON_PATH).value(PRODUCT_PRICE_ALT))
                .andExpect(jsonPath(PRODUCT_STOCK_JSON_PATH).value(PRODUCT_STOCK_ALT));
    }

    @Test
    void testDeleteProductById() throws Exception {
        Product product = productRepository.save(new Product(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK));

        mockMvc.perform(delete(PRODUCT_ID_PATH, product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<Product> deletedProduct = productRepository.findById(product.getId());
        assertTrue(deletedProduct.isEmpty());
    }
}