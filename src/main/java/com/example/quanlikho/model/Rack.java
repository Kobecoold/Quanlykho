package com.example.quanlikho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name="racks")
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String aisleCode;

    private Double height;

    private Double capacity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rack_id")
    @JsonBackReference(value = "aisle-rack")
    private Aisle aisle;

    @OneToMany(mappedBy = "rack", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "rack-shelf")
    private List<Shelf> shelves = new ArrayList<>();

    private void addShelf(Shelf shelf){
        shelves.add(shelf);
        shelf.setRack(this);
    }

    private void removeShelf(Shelf shelf){
        shelves.remove(shelf);
        shelf.setRack(null);
    }


}
