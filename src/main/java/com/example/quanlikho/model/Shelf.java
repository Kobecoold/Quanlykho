package com.example.quanlikho.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "compartments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shelve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @OneToMany(mappedBy = "compartment")
    private List<Pallet> pallets;
}