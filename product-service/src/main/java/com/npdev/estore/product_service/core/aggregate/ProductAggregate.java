package com.npdev.estore.product_service.core.aggregate;

import com.npdev.estore.product_service.command.CreateProductCommand;
import com.npdev.estore.product_service.core.event.ProductCreatedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Slf4j
@Aggregate
@NoArgsConstructor // required by Axon framework
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand) {
        // when the CreateProductCommand is dispatched, this constructor will be used to create an aggregate
        if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price cannot be less or equal than zero.");
        }

        if (StringUtils.isBlank(createProductCommand.getTitle())) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
        // copy properties with the same name from source to dest object
        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);
        // dispatch the event that will trigger the EventSourcingHandler
        AggregateLifecycle.apply(productCreatedEvent);

//        if (true) {
//            log.info("ProductAggregate::Throwing an exception");
//            throw new RuntimeException("Runtime exception");
//        }
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        // initialize the latest state of the aggregate with the new event
        // when the exception happens, an event sourcing handler is not called
        // Axon does not immediately trigger this handler, but it stages it in case some error pops out
        productId = productCreatedEvent.getProductId();
        title = productCreatedEvent.getTitle();
        price = productCreatedEvent.getPrice();
        quantity = productCreatedEvent.getQuantity();
        // no business logic, just update the aggregate state
    }
}
