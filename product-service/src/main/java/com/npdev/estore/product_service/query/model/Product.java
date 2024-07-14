package com.npdev.estore.product_service.query.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 503285530145357203L;

    @Id
    @Column(unique = true)
    private String id;
    private String title;
    private BigDecimal price;
    private Integer quantity;

}
