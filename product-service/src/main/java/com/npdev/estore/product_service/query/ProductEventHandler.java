package com.npdev.estore.product_service.query;

import com.npdev.estore.product_service.core.event.ProductCreatedEvent;
import com.npdev.estore.product_service.query.model.Product;
import com.npdev.estore.product_service.query.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ProductEventHandler {

    private final ProductRepository productRepository;

    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        Product product = Product.builder()
                .id(productCreatedEvent.getProductId())
                .price(productCreatedEvent.getPrice())
                .title(productCreatedEvent.getTitle())
                .quantity(productCreatedEvent.getQuantity())
                .build();

        // this can fail
        productRepository.save(product);
    }
}
