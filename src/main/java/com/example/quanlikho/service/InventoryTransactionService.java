package com.example.quanlikho.service;

import com.example.quanlikho.model.InventoryTransaction;
import com.example.quanlikho.model.Product;
import com.example.quanlikho.repository.InventoryTransactionRepository;
import com.example.quanlikho.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    @Autowired
    public InventoryTransactionService(InventoryTransactionRepository transactionRepository,
                                     ProductRepository productRepository) {
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public InventoryTransaction createTransaction(InventoryTransaction transaction) {
        // Validate transaction
        validateTransaction(transaction);

        // Update product quantity
        updateProductQuantity(transaction);

        // Set transaction date if not set
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }

        return transactionRepository.save(transaction);
    }

    public List<InventoryTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public InventoryTransaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    public List<InventoryTransaction> getTransactionsByProduct(Long productId) {
        return transactionRepository.findByProductId(productId);
    }

    public List<InventoryTransaction> getTransactionsByType(String transactionType) {
        return transactionRepository.findByTransactionType(transactionType);
    }

    public List<InventoryTransaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByTransactionDateBetween(startDate, endDate);
    }

    public List<InventoryTransaction> getTransactionsByProductAndDateRange(Long productId, 
                                                                         LocalDateTime startDate, 
                                                                         LocalDateTime endDate) {
        return transactionRepository.findTransactionsByProductAndDateRange(productId, startDate, endDate);
    }

    public Integer getTotalQuantityByProductAndType(Long productId, String transactionType) {
        return transactionRepository.sumQuantityByProductAndType(productId, transactionType);
    }

    private void validateTransaction(InventoryTransaction transaction) {
        if (transaction.getProduct() == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (transaction.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (transaction.getTransactionType() == null || 
            (!transaction.getTransactionType().equals("IMPORT") && 
             !transaction.getTransactionType().equals("EXPORT"))) {
            throw new IllegalArgumentException("Transaction type must be either IMPORT or EXPORT");
        }
        if (transaction.getCreatedBy() == null) {
            throw new IllegalArgumentException("Created by user cannot be null");
        }
    }

    @Transactional
    private void updateProductQuantity(InventoryTransaction transaction) {
        Product product = transaction.getProduct();
        int quantityChange = transaction.getQuantity();
        
        if (transaction.getTransactionType().equals("EXPORT")) {
            // Check if enough quantity is available
            if (product.getQuantity() < quantityChange) {
                throw new IllegalStateException("Not enough quantity available for export");
            }
            quantityChange = -quantityChange;
        }
        
        product.setQuantity(product.getQuantity() + quantityChange);
        productRepository.save(product);
    }
} 