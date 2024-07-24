package com.npdev.estore.order_service.query.model;

import com.npdev.estore.order_service.command.dto.internal.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "\"order\"") // order is a reserved keyword, name must be explicitly set
public class Order {

    @Id
    @Column(unique = true)
    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
