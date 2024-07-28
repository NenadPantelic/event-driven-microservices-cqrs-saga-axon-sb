package com.npdev.estore.product_service.command;

import com.npdev.estore.product_service.core.event.ProductCreatedEvent;
import com.npdev.estore.product_service.command.model.ProductLookup;
import com.npdev.estore.product_service.command.repository.ProductLookupRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ProcessingGroup("product-group") // logically group event handlers together; the default value is a package name
// processing groups define a processing token to make sure the same events are not processed multiple times by
// different threads
public class ProductLookupEventHandler {

    private final ProductLookupRepository productLookupRepository;

    public ProductLookupEventHandler(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) {
        log.info("Handling ProductCreatedEvent {}", event);
        ProductLookup productLookup = new ProductLookup(event.getProductId(), event.getTitle());
        productLookupRepository.save(productLookup);
    }

    @ResetHandler
    public void reset() {
        log.info("Resetting event handling (before replay)...");
        productLookupRepository.deleteAll();
    }
}
