package com.npdev.estore.order_service.query;

import com.npdev.estore.order_service.core.data.OrderSummary;
import com.npdev.estore.order_service.query.model.Order;
import com.npdev.estore.order_service.query.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderQueryHandler {

    private final OrderRepository orderRepository;

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
        log.info("Handling FindOrderQuery: {}", findOrderQuery);
        Order order = orderRepository.findById(findOrderQuery.getOrderId())
                .orElseThrow(() -> new RuntimeException(
                        String.format("Order[id = %s] not found.", findOrderQuery.getOrderId()))
                );

        return new OrderSummary(order.getOrderId(), order.getOrderStatus(), "");
    }
}
