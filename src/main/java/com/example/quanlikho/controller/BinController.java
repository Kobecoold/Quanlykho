package com.example.quanlikho.controller;

import com.example.quanlikho.model.Pallet;
import com.example.quanlikho.service.PalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PalletController {

    @Autowired
    private PalletService palletService;

    @GetMapping("/pallets")
    public ResponseEntity<List<Pallet>> getAllPallets() {
        List<Pallet> pallets = palletService.getAllPallets();
        return new ResponseEntity<>(pallets, HttpStatus.OK);
    }

    @GetMapping("/shelves/{shelfId}/pallets")
    public ResponseEntity<List<Pallet>> getPalletsByShelfId(@PathVariable Long shelfId) {
        List<Pallet> pallets = palletService.getPalletsByShelfId(shelfId);
        return new ResponseEntity<>(pallets, HttpStatus.OK);
    }

    @GetMapping("/pallets/{id}")
    public ResponseEntity<Pallet> getPalletById(@PathVariable Long id) {
        Optional<Pallet> pallet = palletService.getPalletById(id);

        return pallet.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/shelves/{shelfId}/pallets")
    public ResponseEntity<Pallet> createPallet(@PathVariable Long shelfId, @RequestBody Pallet pallet) {
        try {
            Pallet newPallet = palletService.createPallet(shelfId, pallet);
            return new ResponseEntity<>(newPallet, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/pallets/{id}")
    public ResponseEntity<Pallet> updatePallet(@PathVariable Long id, @RequestBody Pallet pallet) {
        try {
            Pallet updatedPallet = palletService.updatePallet(id, pallet);
            return new ResponseEntity<>(updatedPallet, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/pallets/{id}")
    public ResponseEntity<HttpStatus> deletePallet(@PathVariable Long id) {
        try {
            palletService.deletePallet(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}