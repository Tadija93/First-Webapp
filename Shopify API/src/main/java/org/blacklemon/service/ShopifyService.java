package org.blacklemon.service;

import org.blacklemon.config.ShopifyConfig;
import org.blacklemon.model.Product;
import org.blacklemon.model.Variant; // Ensure you import this
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ShopifyService {

    private static final Logger logger = LoggerFactory.getLogger(ShopifyService.class);
    private final ShopifyConfig config;
    private final RestTemplate restTemplate;

    @Autowired
    public ShopifyService(ShopifyConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }

    // Fetches all products from the PIM store
    public List<Product> getAllProductsFromPim() {
        return fetchProducts(config.getPimStoreUrl(), config.getPimAccessToken());
    }

    // Creates a new product in the Receiver Shopify store
    public Product createProductInReceiver(Product product) {
        sanitizeVariantIds(product);
        return sendProduct(config.getReceiverStoreUrl() + "/products.json", product, HttpMethod.POST);
    }

    // Updates an existing product in the Receiver store using its ID
    public Product updateProductInReceiver(Long id, Product product) {
        sanitizeVariantIds(product);
        String url = config.getReceiverStoreUrl() + "/products/" + id + ".json";
        return sendProduct(url, product, HttpMethod.PUT);
    }

    // Tries to find a product in the Receiver store by its handle (unique per product)
    public Product getProductByHandleInReceiver(String handle) {
        String url = config.getReceiverStoreUrl() + "/products.json?handle=" + handle;
        return fetchSingleProduct(url, config.getReceiverAccessToken(), handle);
    }

    // Syncs products from PIM to Receiver: update if exists, create if new
    public int[] syncProducts() {
        List<Product> products = getAllProductsFromPim();
        int created = 0, updated = 0;

        for (Product p : products) {
            try {
                Product existing = getProductByHandleInReceiver(p.getHandle());
                updateProductInReceiver(existing.getId(), p);
                updated++;
            } catch (ProductNotFoundException e) {
                createProductInReceiver(p);
                created++;
            } catch (ShopifyApiException e) {
                logger.error("Sync error for {}: {}", p.getHandle(), e.getMessage());
            }
        }

        return new int[]{created, updated};
    }

    // General method to fetch products with Shopify API from a given store (PIM or Receiver)
    private List<Product> fetchProducts(String baseUrl, String token) {
        String url = baseUrl + "/products.json";
        HttpHeaders headers = createHeaders(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ProductListResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ProductListResponse.class);
            return Optional.ofNullable(response.getBody()).map(ProductListResponse::getProducts).orElse(Collections.emptyList());
        } catch (RestClientException e) {
            logAndThrow("PIM", e);
            return Collections.emptyList();
        }
    }

    // Sends a product to Shopify (used for both POST and PUT)
    private Product sendProduct(String url, Product product, HttpMethod method) {
        HttpHeaders headers = createHeaders(config.getReceiverAccessToken());
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(Map.of("product", product), headers);

        try {
            ResponseEntity<ProductResponse> response = restTemplate.exchange(url, method, entity, ProductResponse.class);
            return Optional.ofNullable(response.getBody()).map(ProductResponse::getProduct)
                    .orElseThrow(() -> new ShopifyApiException("Empty response from Shopify", null));
        } catch (RestClientException e) {
            logAndThrow("Receiver", e);
            return null;
        }
    }

    // Fetch a single product from Shopify by handle
    private Product fetchSingleProduct(String url, String token, String handle) {
        HttpHeaders headers = createHeaders(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ProductListResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ProductListResponse.class);
            List<Product> products = Optional.ofNullable(response.getBody()).map(ProductListResponse::getProducts).orElse(Collections.emptyList());
            return products.stream().findFirst().orElseThrow(() -> new ProductNotFoundException("Product with handle '" + handle + "' not found."));
        } catch (RestClientException e) {
            logAndThrow("Receiver", e);
            return null;
        }
    }

    // Removes all variant IDs before sending to avoid "invalid ID" errors in Shopify
    private void sanitizeVariantIds(Product product) {
        if (product.getVariants() != null) {
            for (Variant variant : product.getVariants()) {
                logger.debug("Removing ID for variant: SKU={}, original ID={}", variant.getSku(), variant.getId());
                variant.setId(null); // prevent 422 errors due to foreign variant IDs
            }
        }
    }

    // Logs and throws a custom ShopifyApiException
    private void logAndThrow(String source, RestClientException e) {
        logger.error("{} error: {}", source, e.getMessage(), e);
        throw new ShopifyApiException(source + " error", e);
    }

    // Creates authentication headers for Shopify API calls
    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Shopify-Access-Token", accessToken);
        headers.set("User-Agent", "ShopifySyncClient/1.0");
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // Inner classes to map responses from Shopify
    public static class ProductListResponse {
        private List<Product> products;
        public List<Product> getProducts() { return products; }
        public void setProducts(List<Product> products) { this.products = products; }
    }

    public static class ProductResponse {
        private Product product;
        public Product getProduct() { return product; }
        public void setProduct(Product product) { this.product = product; }
    }

    // Custom exceptions for better error handling
    public static class ShopifyApiException extends RuntimeException {
        public ShopifyApiException(String message, Throwable cause) { super(message, cause); }
    }

    public static class ProductNotFoundException extends ShopifyApiException {
        public ProductNotFoundException(String message) { super(message, null); }
    }

    // Public method to fetch all products from the receiver store
    public List<Product> fetchProductsFromReceiver() {
        return fetchProducts(config.getReceiverStoreUrl(), config.getReceiverAccessToken());
    }
}
