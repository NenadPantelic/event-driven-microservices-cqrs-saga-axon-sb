package com.npdev.estore.product_service.query;

import com.npdev.estore.product_service.query.dto.ProductDTO;
import com.npdev.estore.product_service.query.model.Product;
import com.npdev.estore.product_service.query.repository.ProductRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductQueryHandler {

    private final ProductRepository productRepository;

    public ProductQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @QueryHandler
    public List<ProductDTO> findProducts(FindProductsQuery findProductsQuery) {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> ProductDTO.builder()
                        .id(product.getId())
                        .price(product.getPrice())
                        .title(product.getTitle())
                        .quantity(product.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }
}
