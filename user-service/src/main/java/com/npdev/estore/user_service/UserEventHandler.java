package com.npdev.estore.user_service;

import com.npdev.estore.core.model.PaymentDetails;
import com.npdev.estore.core.model.User;
import com.npdev.estore.core.query.FetchUserPaymentDetailsQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserEventHandler {

    @QueryHandler
    public User handlePaymentDetailsQuery(FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery) {
        log.info("Handling FetchUserPaymentDetailsQuery: {}", fetchUserPaymentDetailsQuery);
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("card123")
                .cvv("123")
                .name("Nenad Pantelic")
                .validUntilMonth(12)
                .validUntilYear(2026)
                .build();

        return User.builder()
                .paymentDetails(paymentDetails)
                .firstName("Nenad")
                .lastName("Pantelic")
                .userId(fetchUserPaymentDetailsQuery.getUserId())
                .build();
    }
}
