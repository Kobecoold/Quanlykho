package com.example.quanlikho.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, unique = true)
    private String sku; // Mã sản phẩm

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    private String unit; // Đơn vị tính

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "min_quantity")
    private Integer minQuantity; // Số lượng tối thiểu cần duy trì trong kho

    private boolean active = true;
} 