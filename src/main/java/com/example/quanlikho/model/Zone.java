package com.example.quanlikho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name="zones")
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String zoneCode;

    private String location;

    private int capacity;

    private String zoneType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    @JsonBackReference(value = "warehouse-zone")
    private Warehouse warehouse;



    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "zone-aisle")
    private List<Aisle> aisles = new ArrayList<>();

    public void addAlise(Aisle aisle) {
        aisles.add(aisle);
        aisle.setZone(this);
    }

    public void removeShelf(Aisle aisle) {
        aisles.remove(aisle);
        aisle.setZone(null);
    }
}
