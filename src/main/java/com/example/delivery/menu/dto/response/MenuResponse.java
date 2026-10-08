package com.example.delivery.menu.dto.response;

import com.example.delivery.menu.entity.Menu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MenuResponse {
    private final Long menuId;
    private final String name;
    private final Integer price;
    private final String description;

    public MenuResponse(Menu menu) {
        this.menuId = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.description = menu.getDescription();
    }
}
