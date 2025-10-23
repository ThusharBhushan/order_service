package com.orderservice.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.orderservice.service.OrderService;

@Component
public class OrderStatusScheduler {
    private static final Logger log = LoggerFactory.getLogger(OrderStatusScheduler.class);
    private final OrderService service;

    public OrderStatusScheduler(OrderService service) {
        this.service = service;
    }

    // runs every 5 minutes
    @Scheduled(fixedRateString = "${scheduler.order-status.fixed-rate:300000}")
    public void promotePendingOrders() {
        log.info("Scheduler: promoting all PENDING -> PROCESSING");
        service.movePendingToProcessing();
    }
}
