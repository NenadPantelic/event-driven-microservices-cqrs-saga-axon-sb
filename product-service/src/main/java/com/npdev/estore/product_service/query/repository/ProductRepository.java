package com.npdev.estore.product_service.query.repository;

import com.npdev.estore.product_service.query.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findByIdOrTitle(String id, String title);
}
