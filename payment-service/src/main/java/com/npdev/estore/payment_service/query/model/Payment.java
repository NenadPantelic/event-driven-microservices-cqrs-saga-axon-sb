package com.npdev.estore.payment_service.query.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Entity
public class Payment {

    @Id
    private String paymentId;
    @Column
    private String orderId;
}
