package com.npdev.estore.product_service.query.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDTO {
    private String id;
    private String title;
    private BigDecimal price;
    private Integer quantity;
}
