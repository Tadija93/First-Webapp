package org.blacklemon.controller;

import org.blacklemon.model.Product;
import org.blacklemon.service.ShopifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shopify")
public class ShopifyController {

    private static final Logger logger = LoggerFactory.getLogger(ShopifyController.class);
    private final ShopifyService shopifyService;

    @Autowired
    public ShopifyController(ShopifyService shopifyService) {
        this.shopifyService = shopifyService;
    }

    // Fetch all products from the PIM store
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        logger.info("Fetching all products from PIM");
        return ResponseEntity.ok(shopifyService.getAllProductsFromPim());
    }

    // Update an individual product in the Receiver store by ID
    @PostMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product updatedProduct) {
        logger.info("Updating product ID {}", productId);
        return ResponseEntity.ok(shopifyService.updateProductInReceiver(productId, updatedProduct));
    }

    // Sync all products from PIM → Receiver (create or update depending on presence)
    @PostMapping("/sync-products")
    public ResponseEntity<String> syncProducts() {
        int[] result = shopifyService.syncProducts();
        return ResponseEntity.ok("Products synchronized. Created: " + result[0] + ", Updated: " + result[1]);
    }

    // Fetch all products from the Receiver store
    @GetMapping("/receiver/products")
    public ResponseEntity<List<Product>> getAllReceiverProducts() {
        return ResponseEntity.ok(shopifyService.fetchProductsFromReceiver());
    }

    // Simple health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Application is running!");
    }
}