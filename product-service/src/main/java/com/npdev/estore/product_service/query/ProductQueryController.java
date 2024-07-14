package com.npdev.estore.product_service.query;

import com.npdev.estore.product_service.query.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/products-query")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductQueryController {

    private final QueryGateway queryGateway;

    @GetMapping
    public List<ProductDTO> listProducts() {
        log.info("Listing products...");
        FindProductsQuery findProductsQuery = new FindProductsQuery();
        return queryGateway.query(
                findProductsQuery, ResponseTypes.multipleInstancesOf(ProductDTO.class)
        ).join();
    }

}
