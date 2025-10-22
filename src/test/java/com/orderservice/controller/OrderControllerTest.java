package com.orderservice.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.CreateOrderRequest;
import com.orderservice.dto.OrderDTO;
import com.orderservice.dto.OrderItemDTO;
import com.orderservice.model.Order;
import com.orderservice.model.OrderItem;
import com.orderservice.model.constant.OrderStatus;
import com.orderservice.service.OrderService;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

	@Mock
	private OrderService orderService;

	@Mock
	private ModelMapper modelMapper = new ModelMapper();

	@InjectMocks
	private OrderController orderController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	private Order order;
	private OrderDTO orderDto;
	private CreateOrderRequest createOrderRequest;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
		objectMapper = new ObjectMapper();

		// Mock OrderItem + Order
		OrderItem item = new OrderItem("P100", 2, BigDecimal.valueOf(199.99));
		order = new Order();
		order.addItem(item);
		order.setStatus(OrderStatus.PENDING);

		// DTO version
		OrderItemDTO itemDto = new OrderItemDTO();
		itemDto.setId((long)10);
		itemDto.setProductId("P100");
		itemDto.setQuantity(2);
		itemDto.setPrice(BigDecimal.valueOf(199.99));

		orderDto = new OrderDTO();
		orderDto.setId("abc123");
		orderDto.setStatus(OrderStatus.PENDING);
		orderDto.setCreatedAt(Instant.now());
		orderDto.setItems(List.of(itemDto));

		createOrderRequest = new CreateOrderRequest();
		createOrderRequest.setItems(List.of(itemDto));
	}

	private String asJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}

	@Test
	@DisplayName("Create Order - should return 201 with JSON body")
	void testCreateOrder() throws Exception {
		when(orderService.createOrder(anyList())).thenReturn(order);

		mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(asJson(createOrderRequest)))
				.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("Get Order by ID - should return 200 with correct JSON")
	void testGetOrderById() throws Exception {
		when(orderService.getOrder("abc123")).thenReturn(Optional.of(order));

		mockMvc.perform(get("/api/orders/abc123")).andExpect(status().isOk());
	}

	@Test
	@DisplayName(" Get Order by ID - not found returns 404")
	void testGetOrderById_NotFound() throws Exception {
		when(orderService.getOrder("xyz")).thenReturn(Optional.empty());

		mockMvc.perform(get("/api/orders/xyz")).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("List Orders - should return list of orders")
	void testListOrders() throws Exception {
		when(orderService.listOrders(any())).thenReturn(List.of(order));

		mockMvc.perform(get("/api/orders")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("Update Order Status - should return updated order")
	void testUpdateOrderStatus() throws Exception {
		when(orderService.updateStatus(eq("abc123"), eq(OrderStatus.SHIPPED))).thenReturn(Optional.of(order));

		mockMvc.perform(patch("/api/orders/abc123/status").param("status", "SHIPPED")).andExpect(status().isOk()); 
																											
	}

	@Test
	@DisplayName("Cancel Order - success")
	void testCancelOrder_Success() throws Exception {
		when(orderService.cancelOrder("abc123")).thenReturn(true);

		mockMvc.perform(post("/api/orders/abc123/cancel")).andExpect(status().isOk())
				.andExpect(content().string(containsString("Cancelled")));
	}

	@Test
	@DisplayName("Cancel Order - should return 409 Conflict when invalid")
	void testCancelOrder_Failure() throws Exception {
		when(orderService.cancelOrder("xyz")).thenReturn(false);

		mockMvc.perform(post("/api/orders/xyz/cancel")).andExpect(status().isConflict())
				.andExpect(content().string(containsString("Can only cancel PENDING orders")));
	}
}
