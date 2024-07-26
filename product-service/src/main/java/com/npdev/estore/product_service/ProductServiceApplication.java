package com.npdev.estore.product_service;

import com.npdev.estore.product_service.command.interceptor.CreateProductCommandInterceptor;
import com.npdev.estore.product_service.config.AxonXstreamConfig;
import com.npdev.estore.product_service.core.errorhandling.EventErrorHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDiscoveryClient
@Import({AxonXstreamConfig.class})
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    @Autowired
    public void registerCreateProductCommandInterceptor(ApplicationContext context, CommandBus commandBus) {
        commandBus.registerDispatchInterceptor(context.getBean(CreateProductCommandInterceptor.class));
    }

    @Autowired
    public void configure(EventProcessingConfigurer configurer) {
        configurer.registerListenerInvocationErrorHandler(
                "product-group", configuration -> new EventErrorHandler()
        );

        // in case we do not need a custom error handler (this one propagates exceptions in a bubble-up manner
        // configurer.registerListenerInvocationErrorHandler(
        // "product-group", configuration -> PropagatingErrorHandler.instance()
        // );
    }

}
