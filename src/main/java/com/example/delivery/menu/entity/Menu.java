package com.example.delivery.menu.entity;

import com.example.delivery.global.entity.BaseEntity;
import com.example.delivery.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column
    private String description;

    @Column(nullable = false)
    private boolean deleted = false;     // 메뉴 삭제 : 실제로 지우지 않고 "삭제됨" 표시, Soft Delete 표시

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;     // 사장님만

    public Menu(String name, Integer price, String description, User owner) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.owner = owner;
    }
}
