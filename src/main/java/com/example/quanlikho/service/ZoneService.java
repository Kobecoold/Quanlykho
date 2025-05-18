package com.example.quanlikho.service;

import com.example.quanlikho.model.Aisle;
import com.example.quanlikho.model.Bin;
import com.example.quanlikho.model.Rack;
import com.example.quanlikho.model.Shelf;
import com.example.quanlikho.model.Zone;
import java.util.List;
import java.util.Optional;

public interface ZoneService {

    /**
     * Create a new zone
     * @param zone the zone to create
     * @return the created zone
     */
    Zone createZone(Zone zone);

    /**
     * Get a zone by id
     * @param zoneId the zone id
     * @return optional containing the zone if found
     */
    Optional<Zone> getZoneById(Long zoneId);

    /**
     * Get a zone by code
     * @param zoneCode the zone code
     * @return optional containing the zone if found
     */
    Optional<Zone> getZoneByCode(String zoneCode);

    /**
     * Get a zone with all nested entities
     * @param zoneId the zone id
     * @return optional containing the zone with all nested entities if found
     */
    Optional<Zone> getZoneWithNestedEntities(Long zoneId);

    /**
     * Get all zones
     * @return list of all zones
     */
    List<Zone> getAllZones();

    /**
     * Get all zones by warehouse id
     * @param warehouseId the warehouse id
     * @return list of zones
     */
    List<Zone> getZonesByWarehouseId(Long warehouseId);

    /**
     * Get all zones by zone type
     * @param zoneType the zone type
     * @return list of zones
     */
    List<Zone> getZonesByType(String zoneType);

    /**
     * Update a zone
     * @param zoneId the zone id
     * @param zone the updated zone data
     * @return the updated zone
     */
    Zone updateZone(Long zoneId, Zone zone);

    /**
     * Delete a zone
     * @param zoneId the zone id
     * @return true if deleted successfully
     */
    boolean deleteZone(Long zoneId);

    /**
     * Add an aisle to a zone
     * @param zoneId the zone id
     * @param aisle the aisle to add
     * @return the updated zone
     */
    Zone addAisleToZone(Long zoneId, Aisle aisle);

    /**
     * Update an aisle in a zone
     * @param zoneId the zone id
     * @param aisleId the aisle id
     * @param aisle the updated aisle data
     * @return the updated zone
     */
    Zone updateAisleInZone(Long zoneId, Long aisleId, Aisle aisle);

    /**
     * Remove an aisle from a zone
     * @param zoneId the zone id
     * @param aisleId the aisle id
     * @return the updated zone
     */
    Zone removeAisleFromZone(Long zoneId, Long aisleId);

    /**
     * Add a rack to an aisle in a zone
     * @param zoneId the zone id
     * @param aisleId the aisle id
     * @param rack the rack to add
     * @return the updated zone
     */
    Zone addRackToAisle(Long zoneId, Long aisleId, Rack rack);

    /**
     * Update a rack in an aisle
     * @param zoneId the zone id
     * @param aisleId the aisle id
     * @param rackId the rack id
     * @param rack the updated rack data
     * @return the updated zone
     */
    Zone updateRackInAisle(Long zoneId, Long aisleId, Long rackId, Rack rack);

    /**
     * Remove a rack from an aisle
     * @param zoneId the zone id
     * @param aisleId the aisle id
     * @param rackId the rack id
     * @return the updated zone
     */
    Zone removeRackFromAisle(Long zoneId, Long aisleId, Long rackId);

    /**
     * Add a shelf to a rack
     * @param zoneId the zone id
     * @param aisleId the aisle id
     * @param rackId the rack id
     * @param shelf the shelf to add
     * @return the updated zone
     */
    Zone addShelfToRack(Long zoneId, Long aisleId, Long rackId, Shelf shelf);

    /**
     * Update a shelf in a rack
     * @param zoneId the zone id
     * @param aisleId the aisle id
     * @param rackId the rack id
     * @param shelfId the shelf id
     * @param shelf the updated shelf data
     * @return the updated zone
     */
    Zone updateShelfInRack(Long zoneId, Long aisleId, Long rackId, Long shelfId, Shelf shelf);

    /**
     * Remove a shelf from a rack
     * @param zoneId the zone id
     * @param aisleId the aisle id
     * @param rackId the rack id
     * @param shelfId the shelf id
     * @return the updated zone
     */
    Zone removeShelfFromRack(Long zoneId, Long aisleId, Long rackId, Long shelfId);

    /**
     * Add a bin to a shelf
     * @param zoneId the zone id
     * @param aisleId the aisle id
     * @param rackId the rack id
     * @param shelfId the shelf id
     * @param bin the bin to add
     * @return the updated zone
     */
    Zone addBinToShelf(Long zoneId, Long aisleId, Long rackId, Long shelfId, Bin bin);

    /**
     * Update a bin in a shelf
     * @param zoneId the zone id
     * @param aisleId the aisle id
     * @param rackId the rack id
     * @param shelfId the shelf id
     * @param binId the bin id
     * @param bin the updated bin data
     * @return the updated zone
     */
    Zone updateBinInShelf(Long zoneId, Long aisleId, Long rackId, Long shelfId, Long binId, Bin bin);

    /**
     * Remove a bin from a shelf
     * @param zoneId the zone id
     * @param aisleId the aisle id
     * @param rackId the rack id
     * @param shelfId the shelf id
     * @param binId the bin id
     * @return the updated zone
     */
    Zone removeBinFromShelf(Long zoneId, Long aisleId, Long rackId, Long shelfId, Long binId);

    /**
     * Get zones with available capacity
     * @param minCapacity minimum required capacity
     * @return list of zones with available capacity
     */
    List<Zone> getZonesWithAvailableCapacity(int minCapacity);
}