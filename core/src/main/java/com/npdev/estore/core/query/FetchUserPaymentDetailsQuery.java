package com.npdev.estore.core.query;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FetchUserPaymentDetailsQuery {

    private String userId;
}
