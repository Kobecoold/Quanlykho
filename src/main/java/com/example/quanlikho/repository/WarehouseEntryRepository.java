package com.example.quanlikho.repository;

import com.example.quanlikho.model.WarehouseEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface WarehouseEntryRepository extends JpaRepository<WarehouseEntry, Long> {
    List<WarehouseEntry> findByEntryDateBetween(LocalDateTime start, LocalDateTime end);
}