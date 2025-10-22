package com.orderservice.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orderservice.dto.CreateOrderRequest;
import com.orderservice.dto.OrderDTO;
import com.orderservice.model.Order;
import com.orderservice.model.OrderItem;
import com.orderservice.model.constant.OrderStatus;
import com.orderservice.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService service;
	private final ModelMapper mapper;

	public OrderController(OrderService service, ModelMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<OrderDTO> create(@RequestBody @Valid CreateOrderRequest req) {
		List<OrderItem> items = req.getItems().stream()
			    .map(dto -> {
			        OrderItem item = new OrderItem();
			        item.setProductId(dto.getProductId());
			        item.setQuantity(dto.getQuantity());
			        item.setPrice(dto.getPrice());
			        return item;
			    })
			    .collect(Collectors.toList());

		Order created = service.createOrder(items);
		OrderDTO dto = mapper.map(created, OrderDTO.class);
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	@GetMapping("/{id}")
	public ResponseEntity<OrderDTO> get(@PathVariable String id) {
		Optional<Order> maybe = service.getOrder(id);
		return maybe.map(order -> ResponseEntity.ok(mapper.map(order, OrderDTO.class)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping
	public List<OrderDTO> list(@RequestParam Optional<OrderStatus> status) {
		return service.listOrders(status).stream().map(o -> mapper.map(o, OrderDTO.class)).collect(Collectors.toList());
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<OrderDTO> updateStatus(@PathVariable String id, @RequestParam OrderStatus status) {
		return service.updateStatus(id, status).map(o -> ResponseEntity.ok(mapper.map(o, OrderDTO.class)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping("/{id}/cancel")
	public ResponseEntity<String> cancel(@PathVariable String id) {
		boolean ok = service.cancelOrder(id);
		if (ok) {
			return ResponseEntity.ok("Cancelled");
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).body("Can only cancel PENDING orders or order not found");
	}
}
