package com.npdev.estore.product_service.command.interceptor;

import com.npdev.estore.product_service.command.CreateProductCommand;
import com.npdev.estore.product_service.command.model.ProductLookup;
import com.npdev.estore.product_service.command.repository.ProductLookupRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Slf4j
@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final ProductLookupRepository productLookupRepository;

    public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {
            log.info("Intercepted a message of type: {}", command.getPayloadType());
            if (CreateProductCommand.class.equals(command.getPayloadType())) {
                // this interceptor will intercept any message regardless of the type
                // we are interested only in CreateProductCommand
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
//                if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
//                    throw new IllegalArgumentException("Price cannot be less or equal than zero.");
//                }
//
//                if (StringUtils.isBlank(createProductCommand.getTitle())) {
//                    throw new IllegalArgumentException("Title cannot be empty.");
//                }
                Optional<ProductLookup> productLookup = productLookupRepository.findByProductIdOrTitle(
                        createProductCommand.getProductId(),
                        createProductCommand.getTitle()
                );

                if (productLookup.isPresent()) {
                    throw new IllegalArgumentException(
                            String.format("Product with id %s or title %s already exists.",
                                    createProductCommand.getProductId(), createProductCommand.getTitle()
                            )
                    );
                }

            }
            return command;
        };
    }
}
