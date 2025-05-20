package com.example.quanlikho.controller;

import com.example.quanlikho.model.Revenue;
import com.example.quanlikho.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/revenues")
public class RevenueController {
    
    @Autowired
    private RevenueService revenueService;
    
    @GetMapping("/daily/{date}")
    public ResponseEntity<BigDecimal> getDailyRevenue(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(revenueService.getDailyRevenue(date));
    }
    
    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<BigDecimal> getMonthlyRevenue(
            @PathVariable int year,
            @PathVariable int month) {
        return ResponseEntity.ok(revenueService.getMonthlyRevenue(year, month));
    }
    
    @GetMapping("/yearly/{year}")
    public ResponseEntity<BigDecimal> getYearlyRevenue(@PathVariable int year) {
        return ResponseEntity.ok(revenueService.getYearlyRevenue(year));
    }
    
    @GetMapping("/between")
    public ResponseEntity<List<Revenue>> getRevenuesBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(revenueService.getRevenuesBetweenDates(startDate, endDate));
    }
    
    @GetMapping("/total-between")
    public ResponseEntity<BigDecimal> getTotalRevenueBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(revenueService.getTotalRevenueBetweenDates(startDate, endDate));
    }
} 