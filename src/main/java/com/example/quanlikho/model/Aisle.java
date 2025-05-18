package com.example.quanlikho.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.ArrayList;

@Entity
@Table(name = "aisles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aisle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String aisleCode;

    private String location;

    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    @JsonBackReference(value = "zone-aisle")
    private Zone zone;


    @OneToMany(mappedBy = "aisle", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "warehouse-zone")
    private List<Rack> racks = new ArrayList<>();

    public void addRack(Rack rack) {
        racks.add(rack);
        rack.setAisle(this);
    }

    public void removeRack(Rack rack) {
        racks.remove(rack);
        rack.setAisle(null);
    }
}