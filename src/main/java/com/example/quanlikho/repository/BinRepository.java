package com.example.quanlikho.repository;

import com.example.quanlikho.model.Pallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PalletRepository extends JpaRepository<Pallet, Long> {
    List<Pallet> findByShelfId(Long shelfId);
}