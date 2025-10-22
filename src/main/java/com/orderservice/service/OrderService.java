package com.orderservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.orderservice.model.Order;
import com.orderservice.model.OrderItem;
import com.orderservice.model.constant.OrderStatus;
import com.orderservice.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepo;

    public OrderService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    public Order createOrder(List<OrderItem> items) {
        Order o = new Order();
        items.forEach(o::addItem);
        return orderRepo.save(o);
    }

    public Optional<Order> getOrder(String id) {
        return orderRepo.findById(id);
    }

    public List<Order> listOrders(Optional<OrderStatus> status) {
        return status.map(orderRepo::findByStatus).orElseGet(orderRepo::findAll);
    }

    public Optional<Order> updateStatus(String id, OrderStatus status) {
        return orderRepo.findById(id).map(order -> {
            order.setStatus(status);
            return orderRepo.save(order);
        });
    }

    public boolean cancelOrder(String id) {
        return orderRepo.findById(id).map(order -> {
            if (order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepo.save(order);
                return true;
            }
            return false;
        }).orElse(false);
    }

    public void movePendingToProcessing() {
        List<Order> pending = orderRepo.findByStatus(OrderStatus.PENDING);
        if (!pending.isEmpty()) {
            pending.forEach(o -> o.setStatus(OrderStatus.PROCESSING));
            orderRepo.saveAll(pending);
        }
    }
}
