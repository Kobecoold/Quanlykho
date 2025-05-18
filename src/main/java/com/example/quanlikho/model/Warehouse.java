package com.example.quanlikho.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="warehouses")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String location;

    private String description;

    private int capacity;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "warehouse-zone")
    private List<Zone> zones = new ArrayList<>();


    public void addZone(Zone zone) {
        zones.add(zone);
        zone.setWarehouse(this);
    }

    public void removeSZone(Zone zone) {
        zones.remove(zone);
        zone.setWarehouse(null);
    }
}
