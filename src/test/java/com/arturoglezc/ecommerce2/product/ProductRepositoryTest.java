package com.arturoglezc.ecommerce2.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.arturoglezc.ecommerce2.product.ProductTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK);
    }

    @Test
    void testSaveProduct() {
        Product savedProduct = productRepository.save(testProduct);
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
    }

    @Test
    void testFindById() {
        productRepository.save(testProduct);
        Product foundProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(foundProduct).isEqualTo(testProduct);
    }

    @Test
    void testUpdateProduct() {
        Product savedProduct = productRepository.save(testProduct);

        savedProduct.setName(PRODUCT_NAME_ALT);
        savedProduct.setPrice(PRODUCT_PRICE_ALT);
        savedProduct.setStock(PRODUCT_STOCK_ALT);

        Product updatedProduct = productRepository.save(savedProduct);

        assertThat(updatedProduct.getName()).isEqualTo(PRODUCT_NAME_ALT);
        assertThat(updatedProduct.getPrice()).isEqualTo(PRODUCT_PRICE_ALT);
        assertThat(updatedProduct.getStock()).isEqualTo(PRODUCT_STOCK_ALT);
    }

    @Test
    void testDeleteProduct() {
        Product savedProduct = productRepository.save(testProduct);

        productRepository.deleteById(savedProduct.getId());

        boolean exists = productRepository.findById(savedProduct.getId()).isPresent();
        assertThat(exists).isFalse();
    }

    @Test
    void testFindAllProducts() {
        Product secondProduct = new Product(
                PRODUCT_NAME_ALT,
                PRODUCT_PRICE_ALT,
                PRODUCT_STOCK_ALT);

        productRepository.saveAll(List.of(testProduct, secondProduct));

        List<Product> products = productRepository.findAll();

        assertThat(products).hasSize(2);
        assertThat(products).contains(testProduct, secondProduct);
    }
}