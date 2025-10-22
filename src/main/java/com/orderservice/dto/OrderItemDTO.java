package com.orderservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class OrderItemDTO {
	
    private Long id;

    @NotBlank
    private String productId;

    @Min(1)
    private int quantity;

    @Min(0)
    private BigDecimal price;
    
    

	public OrderItemDTO() {
	}
	

	public OrderItemDTO(@NotBlank String productId, @Min(1) int quantity, @Min(0) BigDecimal price) {
		super();
		this.productId = productId;
		this.quantity = quantity;
		this.price = price;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
    
    

}
