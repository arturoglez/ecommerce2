package com.arturoglezc.ecommerce2.product;

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

import static com.arturoglezc.ecommerce2.product.ProductTestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ProductServiceTest {

    private static final String INVALID_PRODUCT_DATA_MSG = "Invalid product data";

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK);
    }

    @Test
    void testGetProductById_ShouldReturnProduct() {
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        Product foundProduct = productService.getProductById(testProduct.getId());

        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo(PRODUCT_NAME);
        verify(productRepository, times(1)).findById(testProduct.getId());
    }

    @Test
    void testGetProductById_ShouldThrowExceptionIfNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.getProductById(999L));
    }

    @Test
    void testGetAllProducts_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Product> products = Arrays.asList(
                new Product(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK),
                new Product(PRODUCT_NAME_ALT, PRODUCT_PRICE_ALT, PRODUCT_STOCK_ALT));

        Page<Product> page = new PageImpl<>(products, pageable, 2);

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<Product> result = productService.getAllProducts(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(PRODUCT_NAME, result.getContent().getFirst().getName());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void testCreateProduct_ShouldSaveAndReturnProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product savedProduct = productService.createProduct(testProduct);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo(PRODUCT_NAME);
        assertThat(savedProduct.getPrice()).isEqualTo(PRODUCT_PRICE);
        assertThat(savedProduct.getStock()).isEqualTo(PRODUCT_STOCK);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testCreateProduct_ShouldThrowExceptionForInvalidProduct() {
        Product invalidProduct = new Product(null, -100.0, -10);

        when(productRepository.save(any(Product.class)))
                .thenThrow(new IllegalArgumentException(INVALID_PRODUCT_DATA_MSG));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(invalidProduct);
        });

        assertThat(exception.getMessage()).isEqualTo(INVALID_PRODUCT_DATA_MSG);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_ShouldUpdateAndReturnProduct() {
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        Product updatedProduct = new Product(PRODUCT_NAME_ALT, PRODUCT_PRICE_ALT, PRODUCT_STOCK_ALT);
        updatedProduct.setId(testProduct.getId());

        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(testProduct.getId(), updatedProduct);

        assertThat(result.getName()).isEqualTo(PRODUCT_NAME_ALT);
        assertThat(result.getPrice()).isEqualTo(PRODUCT_PRICE_ALT);
        assertThat(result.getStock()).isEqualTo(PRODUCT_STOCK_ALT);

        verify(productRepository, times(1)).findById(testProduct.getId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_ShouldThrowExceptionIfNotFound() {
        Long invalidId = 999L;
        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());

        Product updatedProduct = new Product(PRODUCT_NAME_ALT, PRODUCT_PRICE_ALT, PRODUCT_STOCK_ALT);

        assertThrows(RuntimeException.class, () -> productService.updateProduct(invalidId, updatedProduct));

        verify(productRepository, times(1)).findById(invalidId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_ShouldDeleteProduct() {
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        productService.deleteProduct(testProduct.getId());

        verify(productRepository, times(1)).findById(testProduct.getId());
        verify(productRepository, times(1)).deleteById(testProduct.getId());
    }

    @Test
    void testDeleteProduct_ShouldThrowExceptionIfNotFound() {
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            productService.deleteProduct(testProduct.getId());
        });
    }
}