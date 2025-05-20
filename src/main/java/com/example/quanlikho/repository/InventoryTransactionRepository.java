package com.example.quanlikho.repository;

import com.example.quanlikho.model.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    
    List<InventoryTransaction> findByProductId(Long productId);
    
    List<InventoryTransaction> findByTransactionType(String transactionType);
    
    List<InventoryTransaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<InventoryTransaction> findByCreatedById(Long userId);
    
    List<InventoryTransaction> findBySupplierId(Long supplierId);
    
    @Query("SELECT t FROM InventoryTransaction t WHERE t.product.id = :productId AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<InventoryTransaction> findTransactionsByProductAndDateRange(
        @Param("productId") Long productId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT SUM(t.quantity) FROM InventoryTransaction t WHERE t.product.id = :productId AND t.transactionType = :transactionType")
    Integer sumQuantityByProductAndType(
        @Param("productId") Long productId,
        @Param("transactionType") String transactionType
    );
} 