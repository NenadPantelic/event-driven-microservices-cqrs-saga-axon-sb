package com.npdev.estore.product_service.command.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record NewProduct(@NotBlank(message = "Product title is a required field") String title,
                         @Min(value = 1, message = "Price cannot be lower than 1") BigDecimal price,
                         @Min(value = 1, message = "Quantity cannot be lower than 1")
                         @Max(value = 5, message = "Quantity cannot be larger than 5") Integer quantity) {
}
