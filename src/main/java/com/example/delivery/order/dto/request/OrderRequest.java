package com.example.delivery.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrderRequest {
    @NotNull
    private Long menuId;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotBlank
    private String address;
}