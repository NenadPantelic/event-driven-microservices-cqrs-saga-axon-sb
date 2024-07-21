package com.npdev.estore.product_service.core.errorhandling;

import java.util.Date;

public record ApiError(String message,
                       Date timestamp) {
}
