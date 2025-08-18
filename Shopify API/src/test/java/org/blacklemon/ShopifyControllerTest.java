package org.blacklemon;

import org.blacklemon.controller.ShopifyController;
import org.blacklemon.model.Product;
import org.blacklemon.service.ShopifyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ShopifyControllerTest {

    @Mock
    private ShopifyService shopifyService;

    @InjectMocks
    private ShopifyController shopifyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // <-- This initializes @Mock and @InjectMocks
    }

    @Test
    public void test_get_all_products_handles_empty_list() {
        List<Product> emptyList = Collections.emptyList();
        when(shopifyService.getAllProductsFromPim()).thenReturn(emptyList);

        ResponseEntity<List<Product>> response = shopifyController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(shopifyService).getAllProductsFromPim();
    }

    @Test
    public void test_get_all_products_handles_non_empty_list() {
        List<Product> productList = List.of(new Product(), new Product());
        when(shopifyService.getAllProductsFromPim()).thenReturn(productList);

        ResponseEntity<List<Product>> response = shopifyController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(shopifyService).getAllProductsFromPim();
    }

    @Test
    public void test_update_product_handles_non_existent_id() {
        Long nonExistentId = 999L;
        Product updatedProduct = new Product();

        when(shopifyService.updateProductInReceiver(nonExistentId, updatedProduct))
                .thenThrow(new ShopifyService.ShopifyApiException("Product not found", null));

        try {
            shopifyController.updateProduct(nonExistentId, updatedProduct);
            fail("Expected ShopifyApiException was not thrown");
        } catch (ShopifyService.ShopifyApiException e) {
            assertEquals("Product not found", e.getMessage());
            verify(shopifyService).updateProductInReceiver(nonExistentId, updatedProduct);
        }
    }
}
