package org.blacklemon.service;

import org.blacklemon.config.ShopifyConfig;
import org.blacklemon.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ShopifyServiceTest {

    @Mock private ShopifyConfig shopifyConfig;
    @Mock private RestTemplate restTemplate;

    @InjectMocks private ShopifyService shopifyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductByHandleInReceiver_shouldThrowProductNotFoundException_whenNoProductsReturned() {
        String handle = "non-existent-product";
        String url = "https://receiver-store.myshopify.com/admin/api/2023-01/products.json?handle=" + handle;

        ShopifyService.ProductListResponse response = new ShopifyService.ProductListResponse();
        response.setProducts(Collections.emptyList());

        when(shopifyConfig.getReceiverStoreUrl()).thenReturn("https://receiver-store.myshopify.com/admin/api/2023-01");
        when(shopifyConfig.getReceiverAccessToken()).thenReturn("receiver-token");
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(ShopifyService.ProductListResponse.class)))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        ShopifyService.ProductNotFoundException ex = assertThrows(
                ShopifyService.ProductNotFoundException.class,
                () -> shopifyService.getProductByHandleInReceiver(handle)
        );

        assertTrue(ex.getMessage().contains(handle));
    }

    @Test
    void getAllProductsFromPim_shouldWrapRestExceptionInShopifyApiException() {
        String url = "https://pim-store.myshopify.com/admin/api/2023-01/products.json";
        RestClientException thrown = new RestClientException("Connection refused");

        when(shopifyConfig.getPimStoreUrl()).thenReturn("https://pim-store.myshopify.com/admin/api/2023-01");
        when(shopifyConfig.getPimAccessToken()).thenReturn("pim-token");
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(ShopifyService.ProductListResponse.class)))
                .thenThrow(thrown);

        ShopifyService.ShopifyApiException ex = assertThrows(
                ShopifyService.ShopifyApiException.class,
                () -> shopifyService.getAllProductsFromPim()
        );

        assertEquals("PIM error", ex.getMessage());
        assertSame(thrown, ex.getCause());
    }
}
