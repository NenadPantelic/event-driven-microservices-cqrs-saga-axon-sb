package com.npdev.estore.product_service.query;

import com.npdev.estore.core.event.ProductReservationCanceledEvent;
import com.npdev.estore.core.event.ProductReservedEvent;
import com.npdev.estore.product_service.core.event.ProductCreatedEvent;
import com.npdev.estore.product_service.query.model.Product;
import com.npdev.estore.product_service.query.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
@ProcessingGroup("product-group")
public class ProductEventHandler {

    private final ProductRepository productRepository;

    @ExceptionHandler
    public void handle(Exception e) throws Exception {
        log.error("An Exception occurred: {}", e.getMessage(), e);
        throw e;
    }

    @ExceptionHandler // pay attention, this is from Axon, not Spring
    // it only handles the exceptions from the same event handling class
    public void handle(IllegalArgumentException e) {
        log.error("An IllegalArgumentException occurred: {}", e.getMessage(), e);
        throw e;
    }

    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        Product product = Product.builder()
                .id(productCreatedEvent.getProductId())
                .price(productCreatedEvent.getPrice())
                .title(productCreatedEvent.getTitle())
                .quantity(productCreatedEvent.getQuantity())
                .build();

        // this can fail; if it does, an exception handler above will react
        productRepository.save(product);

        if (false) {
            log.info("ProductEventHandler::Throwing an exception");
            throw new RuntimeException("Runtime exception");
        }
    }

    @EventHandler
    public void on(ProductReservedEvent productReservedEvent) {
        log.info("Handling ProductReservedEvent {}...", productReservedEvent);
        Product product = productRepository
                .findById(productReservedEvent.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        String.format("Product %s not found", productReservedEvent.getProductId()))
                );

        product.setQuantity(product.getQuantity() - productReservedEvent.getQuantity());
        productRepository.save(product);
    }

    @EventHandler
    public void on(ProductReservationCanceledEvent productReservationCanceledEvent) {
        log.info("Handling ProductReservationCanceledEvent {}...", productReservationCanceledEvent);
        Product product = productRepository
                .findById(productReservationCanceledEvent.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        String.format("Product %s not found", productReservationCanceledEvent.getProductId()))
                );

        product.setQuantity(product.getQuantity() + productReservationCanceledEvent.getQuantity());
        productRepository.save(product);
    }

    @ResetHandler
    public void reset() {
        log.info("Resetting event handling (before replay)...");
        productRepository.deleteAll();
    }
}
