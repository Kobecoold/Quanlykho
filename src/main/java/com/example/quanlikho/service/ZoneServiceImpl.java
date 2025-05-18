package com.example.quanlikho.service;

import com.example.quanlikho.exception.ResourceNotFoundException;
import com.example.quanlikho.model.Aisle;
import com.example.quanlikho.model.Bin;
import com.example.quanlikho.model.Rack;
import com.example.quanlikho.model.Shelf;
import com.example.quanlikho.model.Zone;
import com.example.quanlikho.repository.ZoneRepository;
import com.example.quanlikho.service.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;

    @Autowired
    public ZoneServiceImpl(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    @Override
    @Transactional
    public Zone createZone(Zone zone) {
        // Validate zone code uniqueness
        if (zoneRepository.existsByZoneCode(zone.getZoneCode())) {
            throw new IllegalArgumentException("Zone with code " + zone.getZoneCode() + " already exists");
        }

        // Initialize collections if they're null
        if (zone.getAisles() == null) {
            zone.setAisles(List.of());
        }

        return zoneRepository.save(zone);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Zone> getZoneById(Long zoneId) {
        return zoneRepository.findById(zoneId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Zone> getZoneByCode(String zoneCode) {
        return zoneRepository.findByZoneCode(zoneCode);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Zone> getZoneWithNestedEntities(Long zoneId) {
        return zoneRepository.findByIdWithNestedEntities(zoneId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Zone> getZonesByWarehouseId(Long warehouseId) {
        return zoneRepository.findByWarehouseId(warehouseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Zone> getZonesByType(String zoneType) {
        return zoneRepository.findByZoneType(zoneType);
    }

    @Override
    @Transactional
    public Zone updateZone(Long zoneId, Zone zoneDetails) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        // Check if zoneCode is being changed and ensure it's unique
        if (!zone.getZoneCode().equals(zoneDetails.getZoneCode()) &&
                zoneRepository.existsByZoneCode(zoneDetails.getZoneCode())) {
            throw new IllegalArgumentException("Zone with code " + zoneDetails.getZoneCode() + " already exists");
        }

        // Update zone properties
        zone.setZoneCode(zoneDetails.getZoneCode());
        zone.setLocation(zoneDetails.getLocation());
        zone.setCapacity(zoneDetails.getCapacity());
        zone.setZoneType(zoneDetails.getZoneType());

        // Only update warehouse if it's provided
        if (zoneDetails.getWarehouse() != null) {
            zone.setWarehouse(zoneDetails.getWarehouse());
        }

        return zoneRepository.save(zone);
    }

    @Override
    @Transactional
    public boolean deleteZone(Long zoneId) {
        if (!zoneRepository.existsById(zoneId)) {
            return false;
        }

        zoneRepository.deleteById(zoneId);
        return true;
    }

    @Override
    @Transactional
    public Zone addAisleToZone(Long zoneId, Aisle aisle) {
        Zone zone = getZoneWithNestedEntities(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        // Check capacity
        if (zone.getAisles().size() >= zone.getCapacity()) {
            throw new IllegalStateException("Zone has reached its capacity, cannot add more aisles");
        }

        // Set back reference
        aisle.setZone(zone);

        // Add aisle to zone
        zone.getAisles().add(aisle);

        return zoneRepository.save(zone);
    }

    @Override
    @Transactional
    public Zone updateAisleInZone(Long zoneId, Long aisleId, Aisle aisleDetails) {
        Zone zone = getZoneWithNestedEntities(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        Aisle aisle = zone.getAisles().stream()
                .filter(a -> a.getId().equals(aisleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Aisle not found with id: " + aisleId + " in zone: " + zoneId));

        // Update aisle properties
        aisle.setAisleCode(aisleDetails.getAisleCode());
        aisle.setLocation(aisleDetails.getLocation());
        aisle.setCapacity(aisleDetails.getCapacity());

        return zoneRepository.save(zone);
    }

    @Override
    @Transactional
    public Zone removeAisleFromZone(Long zoneId, Long aisleId) {
        Zone zone = getZoneWithNestedEntities(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        boolean removed = zone.getAisles().removeIf(aisle -> aisle.getId().equals(aisleId));

        if (!removed) {
            throw new ResourceNotFoundException("Aisle not found with id: " + aisleId + " in zone: " + zoneId);
        }

        return zoneRepository.save(zone);
    }

    @Override
    @Transactional
    public Zone addRackToAisle(Long zoneId, Long aisleId, Rack rack) {
        Zone zone = getZoneWithNestedEntities(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        Aisle aisle = zone.getAisles().stream()
                .filter(a -> a.getId().equals(aisleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Aisle not found with id: " + aisleId + " in zone: " + zoneId));

        // Check capacity
        if (aisle.getRacks().size() >= aisle.getCapacity()) {
            throw new IllegalStateException("Aisle has reached its capacity, cannot add more racks");
        }

        // Set back reference
        rack.setAisle(aisle);

        // Add rack to aisle
        aisle.getRacks().add(rack);

        return zoneRepository.save(zone);
    }

    @Override
    @Transactional
    public Zone updateRackInAisle(Long zoneId, Long aisleId, Long rackId, Rack rackDetails) {
        Zone zone = getZoneWithNestedEntities(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        Aisle aisle = zone.getAisles().stream()
                .filter(a -> a.getId().equals(aisleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Aisle not found with id: " + aisleId + " in zone: " + zoneId));

        Rack rack = aisle.getRacks().stream()
                .filter(r -> r.getId().equals(rackId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Rack not found with id: " + rackId + " in aisle: " + aisleId));

        // Update rack properties
        rack.setAisleCode(rackDetails.getAisleCode());
        rack.setHeight(rackDetails.getHeight());
        rack.setCapacity(rackDetails.getCapacity());

        return zoneRepository.save(zone);
    }

    @Override
    @Transactional
    public Zone removeRackFromAisle(Long zoneId, Long aisleId, Long rackId) {
        Zone zone = getZoneWithNestedEntities(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        Aisle aisle = zone.getAisles().stream()
                .filter(a -> a.getId().equals(aisleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Aisle not found with id: " + aisleId + " in zone: " + zoneId));

        boolean removed = aisle.getRacks().removeIf(rack -> rack.getId().equals(rackId));

        if (!removed) {
            throw new ResourceNotFoundException("Rack not found with id: " + rackId + " in aisle: " + aisleId);
        }

        return zoneRepository.save(zone);
    }

    @Override
    @Transactional
    public Zone addShelfToRack(Long zoneId, Long aisleId, Long rackId, Shelf shelf) {
        Zone zone = getZoneWithNestedEntities(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        Aisle aisle = zone.getAisles().stream()
                .filter(a -> a.getId().equals(aisleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Aisle not found with id: " + aisleId + " in zone: " + zoneId));

        Rack rack = aisle.getRacks().stream()
                .filter(r -> r.getId().equals(rackId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Rack not found with id: " + rackId + " in aisle: " + aisleId));

        // Check if capacity is reached
        if (rack.getShelves().size() >= rack.getCapacity()) {
            throw new IllegalStateException("Rack has reached its capacity, cannot add more shelves");
        }

        // Set back reference
        shelf.setRack(rack);

        // Add shelf to rack
        rack.getShelves().add(shelf);

        return zoneRepository.save(zone);
    }

    @Override
    @Transactional
    public Zone updateShelfInRack(Long zoneId, Long aisleId, Long rackId, Long shelfId, Shelf shelfDetails) {
        Zone zone = getZoneWithNestedEntities(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        Aisle aisle = zone.getAisles().stream()
                .filter(a -> a.getId().equals(aisleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Aisle not found with id: " + aisleId + " in zone: " + zoneId));

        Rack rack = aisle.getRacks().stream()
                .filter(r -> r.getId().equals(rackId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Rack not found with id: " + rackId + " in aisle: " + aisleId));

        Shelf shelf = rack.getShelves().stream()
                .filter(s -> s.getId().equals(shelfId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Shelf not found with id: " + shelfId + " in rack: " + rackId));

        // Update shelf properties
        shelf.setShelfCode(shelfDetails.getShelfCode());
        shelf.setMaxWeight(shelfDetails.getMaxWeight());
        shelf.setCapacity(shelfDetails.getCapacity());

        return zoneRepository.save(zone);
    }

    @Override
    @Transactional
    public Zone removeShelfFromRack(Long zoneId, Long aisleId, Long rackId, Long shelfId) {
        Zone zone = getZoneWithNestedEntities(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        Aisle aisle = zone.getAisles().stream()
                .filter(a -> a.getId().equals(aisleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Aisle not found with id: " + aisleId + " in zone: " + zoneId));

        Rack rack = aisle.getRacks().stream()
                .filter(r -> r.getId().equals(rackId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Rack not found with id: " + rackId + " in aisle: " + aisleId));

        boolean removed = rack.getShelves().removeIf(shelf -> shelf.getId().equals(shelfId));

        if (!removed) {
            throw new ResourceNotFoundException("Shelf not found with id: " + shelfId + " in rack: " + rackId);
        }

        return zoneRepository.save(zone);
    }

    @Override
    @Transactional
    public Zone addBinToShelf(Long zoneId, Long aisleId, Long rackId, Long shelfId, Bin bin) {
        Zone zone = getZoneWithNestedEntities(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        Aisle aisle = zone.getAisles().stream()
                .filter(a -> a.getId().equals(aisleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Aisle not found with id: " + aisleId + " in zone: " + zoneId));

        Rack rack = aisle.getRacks().stream()
                .filter(r -> r.getId().equals(rackId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Rack not found with id: " + rackId + " in aisle: " + aisleId));

        Shelf shelf = rack.getShelves().stream()
                .filter(s -> s.getId().equals(shelfId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Shelf not found with id: " + shelfId + " in rack: " + rackId));

        // Check if capacity is reached
        if (shelf.getBins().size() >= shelf.getCapacity()) {
            throw new IllegalStateException("Shelf has reached its capacity, cannot add more bins");
        }

        // Set back reference
        bin.setShelf(shelf);

        // Add bin to shelf
        shelf.getBins().add(bin);

        return zoneRepository.save(zone);
    }

    @Override
    @Transactional
    public Zone updateBinInShelf(Long zoneId, Long aisleId, Long rackId, Long shelfId, Long binId, Bin binDetails) {
        Zone zone = getZoneWithNestedEntities(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        Aisle aisle = zone.getAisles().stream()
                .filter(a -> a.getId().equals(aisleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Aisle not found with id: " + aisleId + " in zone: " + zoneId));

        Rack rack = aisle.getRacks().stream()
                .filter(r -> r.getId().equals(rackId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Rack not found with id: " + rackId + " in aisle: " + aisleId));

        Shelf shelf = rack.getShelves().stream()
                .filter(s -> s.getId().equals(shelfId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Shelf not found with id: " + shelfId + " in rack: " + rackId));

        Bin bin = shelf.getBins().stream()
                .filter(b -> b.getId().equals(binId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Bin not found with id: " + binId + " in shelf: " + shelfId));

        // Update bin properties
        bin.setBinCode(binDetails.getBinCode());
        bin.setMaxWeight(binDetails.getMaxWeight());
        bin.setCurrentWeight(binDetails.getCurrentWeight());

        return zoneRepository.save(zone);
    }

    @Override
    @Transactional
    public Zone removeBinFromShelf(Long zoneId, Long aisleId, Long rackId, Long shelfId, Long binId) {
        Zone zone = getZoneWithNestedEntities(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        Aisle aisle = zone.getAisles().stream()
                .filter(a -> a.getId().equals(aisleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Aisle not found with id: " + aisleId + " in zone: " + zoneId));

        Rack rack = aisle.getRacks().stream()
                .filter(r -> r.getId().equals(rackId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Rack not found with id: " + rackId + " in aisle: " + aisleId));

        Shelf shelf = rack.getShelves().stream()
                .filter(s -> s.getId().equals(shelfId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Shelf not found with id: " + shelfId + " in rack: " + rackId));

        boolean removed = shelf.getBins().removeIf(bin -> bin.getId().equals(binId));

        if (!removed) {
            throw new ResourceNotFoundException("Bin not found with id: " + binId + " in shelf: " + shelfId);
        }

        return zoneRepository.save(zone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Zone> getZonesWithAvailableCapacity(int minCapacity) {
        return zoneRepository.findZonesWithAvailableCapacity(minCapacity);
    }
}