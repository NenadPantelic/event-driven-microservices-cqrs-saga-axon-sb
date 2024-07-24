package com.npdev.estore.order_service.command.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record NewOrder(@NotBlank(message = "Product id is a required field") String productId,
                       @Min(value = 1, message = "Quantity cannot be lower than 1") int quantity,
                       @NotBlank(message = "Address id is a required field") String addressId) {
}