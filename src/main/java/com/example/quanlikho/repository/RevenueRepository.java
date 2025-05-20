package com.example.quanlikho.repository;

import com.example.quanlikho.model.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, Long> {
    
    @Query("SELECT SUM(r.amount) FROM Revenue r WHERE r.date BETWEEN ?1 AND ?2")
    BigDecimal getTotalRevenueBetweenDates(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT r FROM Revenue r WHERE r.date BETWEEN ?1 AND ?2 ORDER BY r.date")
    List<Revenue> getRevenuesBetweenDates(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(r.amount) FROM Revenue r WHERE YEAR(r.date) = ?1 AND MONTH(r.date) = ?2")
    BigDecimal getMonthlyRevenue(int year, int month);
    
    @Query("SELECT SUM(r.amount) FROM Revenue r WHERE YEAR(r.date) = ?1")
    BigDecimal getYearlyRevenue(int year);
} 