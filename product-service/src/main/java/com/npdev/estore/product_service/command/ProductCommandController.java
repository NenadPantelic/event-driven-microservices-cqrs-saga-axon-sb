package com.npdev.estore.product_service.command;

import com.npdev.estore.product_service.command.dto.NewProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductCommandController {

    private final Environment env;
    private final CommandGateway commandGateway;

    @PostMapping
    public String createProduct(@RequestBody NewProduct newProduct) {
        log.info("Received a request to create a product: {}", newProduct);
        // this property holds the port that is set dynamically (random one)

        CreateProductCommand createProductCommand = CreateProductCommand.builder()
                .title(newProduct.title())
                .price(newProduct.price())
                .quantity(newProduct.quantity())
                .productId(UUID.randomUUID().toString())
                .build();

        String returnValue;
        try {
            returnValue = commandGateway.sendAndWait(createProductCommand);
        } catch (Exception e) {
            returnValue = e.getLocalizedMessage();
        }
        return String.format("POST createProduct; port = %s, result = %s", env.getProperty("local.server.port"), returnValue);
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
