package com.npdev.estore.order_service.query.repository;

import com.npdev.estore.order_service.query.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
