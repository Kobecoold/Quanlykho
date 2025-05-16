package com.example.quanlikho.service;

import com.example.quanlikho.model.Pallet;
import com.example.quanlikho.model.Shelf;
import com.example.quanlikho.repository.PalletRepository;
import com.example.quanlikho.repository.ShelfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PalletService {

    @Autowired
    private PalletRepository palletRepository;

    @Autowired
    private ShelfRepository shelfRepository;

    public List<Pallet> getAllPallets() {
        return palletRepository.findAll();
    }

    public List<Pallet> getPalletsByShelfId(Long shelfId) {
        return palletRepository.findByShelfId(shelfId);
    }

    public Optional<Pallet> getPalletById(Long id) {
        return palletRepository.findById(id);
    }

    public Pallet createPallet(Long shelfId, Pallet pallet) {
        Shelf shelf = shelfRepository.findById(shelfId)
                .orElseThrow(() -> new RuntimeException("Shelf not found with id: " + shelfId));

        pallet.setShelf(shelf);
        return palletRepository.save(pallet);
    }

    public Pallet updatePallet(Long id, Pallet palletDetails) {
        Pallet pallet = palletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pallet not found with id: " + id));

        pallet.setPalletCode(palletDetails.getPalletCode());
        pallet.setMaxWeight(palletDetails.getMaxWeight());
        pallet.setCurrentWeight(palletDetails.getCurrentWeight());

        return palletRepository.save(pallet);
    }

    public void deletePallet(Long id) {
        Pallet pallet = palletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pallet not found with id: " + id));

        palletRepository.delete(pallet);
    }
}