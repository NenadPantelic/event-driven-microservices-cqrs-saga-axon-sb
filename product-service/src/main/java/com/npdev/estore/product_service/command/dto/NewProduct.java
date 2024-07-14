package com.npdev.estore.product_service.command.dto;

import java.math.BigDecimal;

public record NewProduct(String title,
                         BigDecimal price,
                         Integer quantity) {
}
