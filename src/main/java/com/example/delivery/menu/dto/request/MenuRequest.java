package com.example.delivery.menu.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class MenuRequest {
    @NotBlank
    private String name;        // 메뉴명

    @NotNull    // 이름이 비어 있거나 가격이 1원보다 작으면 400
    @Min(1)
    private Integer price;      // 메뉴 가격

    private String description; // 메뉴 설명
}
