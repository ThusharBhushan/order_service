package com.orderservice.dto;

import java.time.Instant;
import java.util.List;

import com.orderservice.model.constant.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private String id;
    private OrderStatus status;
    private Instant createdAt;
    private List<OrderItemDTO> items;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public OrderStatus getStatus() {
		return status;
	}
	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	public Instant getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	public List<OrderItemDTO> getItems() {
		return items;
	}
	public void setItems(List<OrderItemDTO> items) {
		this.items = items;
	}
    
    


}
