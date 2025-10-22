package com.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orderservice.model.Order;
import com.orderservice.model.OrderItem;
import com.orderservice.model.constant.OrderStatus;
import com.orderservice.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderItem item1, item2;

    @BeforeEach
    void setUp() {
        item1 = new OrderItem("P100", 2, BigDecimal.valueOf(199.99));
        item2 = new OrderItem("P200", 1, BigDecimal.valueOf(99.50));
        order = new Order();
        order.addItem(item1);
        order.addItem(item2);
        order.setStatus(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("✅ Should create an order successfully")
    void testCreateOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order created = orderService.createOrder(List.of(item1, item2));

        assertNotNull(created);
        assertEquals(OrderStatus.PENDING, created.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("✅ Should get order by ID")
    void testGetOrder() {
        when(orderRepository.findById("123")).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrder("123");

        assertTrue(result.isPresent());
        assertEquals(OrderStatus.PENDING, result.get().getStatus());
        verify(orderRepository).findById("123");
    }

    @Test
    @DisplayName("✅ Should cancel pending order successfully")
    void testCancelPendingOrder_Success() {
        when(orderRepository.findById("abc")).thenReturn(Optional.of(order));

        boolean cancelled = orderService.cancelOrder("abc");

        assertTrue(cancelled);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("❌ Should not cancel non-pending order")
    void testCancelNonPendingOrder_Failure() {
        order.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById("xyz")).thenReturn(Optional.of(order));

        boolean cancelled = orderService.cancelOrder("xyz");

        assertFalse(cancelled);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("✅ Should update order status successfully")
    void testUpdateStatus() {
        when(orderRepository.findById("abc")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Optional<Order> updated = orderService.updateStatus("abc", OrderStatus.SHIPPED);

        assertTrue(updated.isPresent());
        assertEquals(OrderStatus.SHIPPED, updated.get().getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("✅ Should move all pending orders to processing")
    void testMovePendingToProcessing() {
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(List.of(order));

        orderService.movePendingToProcessing();

        assertEquals(OrderStatus.PROCESSING, order.getStatus());
        verify(orderRepository).saveAll(anyList());
    }
}
