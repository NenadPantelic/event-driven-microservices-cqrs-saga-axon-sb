package com.npdev.estore.product_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private Environment env;

    @PostMapping
    public String createProduct() {
        // this property holds the port that is set dynamically (random one)
        return String.format("POST createProduct; port = %s", env.getProperty("local.server.port"));
    }

    @GetMapping
    public String getProduct() {
        return String.format("GET getProduct; port = %s", env.getProperty("local.server.port"));
    }

    @PutMapping
    public String updateProduct() {
        return String.format("PUT updateProduct; port = %s", env.getProperty("local.server.port"));
    }


    @DeleteMapping
    public void deleteProduct() {
    }
}
