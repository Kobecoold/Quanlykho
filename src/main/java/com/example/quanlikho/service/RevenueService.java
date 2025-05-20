package com.example.quanlikho.service;

import com.example.quanlikho.model.Revenue;
import com.example.quanlikho.repository.RevenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

@Service
public class RevenueService {
    
    @Autowired
    private RevenueRepository revenueRepository;
    
    public BigDecimal getDailyRevenue(LocalDate date) {
        return revenueRepository.getTotalRevenueBetweenDates(date, date);
    }
    
    public BigDecimal getMonthlyRevenue(int year, int month) {
        return revenueRepository.getMonthlyRevenue(year, month);
    }
    
    public BigDecimal getYearlyRevenue(int year) {
        return revenueRepository.getYearlyRevenue(year);
    }
    
    public List<Revenue> getRevenuesBetweenDates(LocalDate startDate, LocalDate endDate) {
        return revenueRepository.getRevenuesBetweenDates(startDate, endDate);
    }
    
    public BigDecimal getTotalRevenueBetweenDates(LocalDate startDate, LocalDate endDate) {
        return revenueRepository.getTotalRevenueBetweenDates(startDate, endDate);
    }
} 