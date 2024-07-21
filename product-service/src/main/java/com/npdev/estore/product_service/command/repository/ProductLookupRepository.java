package com.npdev.estore.product_service.command.repository;

import com.npdev.estore.product_service.command.model.ProductLookup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductLookupRepository extends JpaRepository<ProductLookup, String> {

    Optional<ProductLookup> findByProductIdOrTitle(String productId, String title);
}
