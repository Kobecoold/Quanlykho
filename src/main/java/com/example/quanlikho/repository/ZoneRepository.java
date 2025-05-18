package com.example.quanlikho.repository;

import com.example.quanlikho.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

    /**
     * Find zone by its unique code
     * @param zoneCode the zone code
     * @return optional containing the zone if found
     */
    Optional<Zone> findByZoneCode(String zoneCode);

    /**
     * Find all zones by warehouse id
     * @param warehouseId the warehouse id
     * @return list of zones
     */
    List<Zone> findByWarehouseId(Long warehouseId);

    /**
     * Find all zones by zone type
     * @param zoneType the zone type
     * @return list of zones
     */
    List<Zone> findByZoneType(String zoneType);

    /**
     * Find zone with all nested entities (aisles, racks, shelves, bins)
     * @param zoneId the zone id
     * @return optional containing the zone with all nested entities if found
     */
    @Query("SELECT z FROM Zone z " +
            "LEFT JOIN FETCH z.aisles a " +
            "LEFT JOIN FETCH a.racks r " +
            "LEFT JOIN FETCH r.shelves s " +
            "LEFT JOIN FETCH s.bins b " +
            "WHERE z.id = :zoneId")
    Optional<Zone> findByIdWithNestedEntities(@Param("zoneId") Long zoneId);

    /**
     * Find zone with all nested entities by zone code
     * @param zoneCode the zone code
     * @return optional containing the zone with all nested entities if found
     */
    @Query("SELECT z FROM Zone z " +
            "LEFT JOIN FETCH z.aisles a " +
            "LEFT JOIN FETCH a.racks r " +
            "LEFT JOIN FETCH r.shelves s " +
            "LEFT JOIN FETCH s.bins b " +
            "WHERE z.zoneCode = :zoneCode")
    Optional<Zone> findByZoneCodeWithNestedEntities(@Param("zoneCode") String zoneCode);

    /**
     * Check if a zone code already exists
     * @param zoneCode the zone code
     * @return true if the zone code exists
     */
    boolean existsByZoneCode(String zoneCode);

    /**
     * Find zones with available capacity
     * @param minCapacity minimum required capacity
     * @return list of zones with available capacity
     */
    @Query("SELECT z FROM Zone z WHERE (z.capacity - " +
            "(SELECT COUNT(a) FROM Aisle a WHERE a.zone.id = z.id)) >= :minCapacity")
    List<Zone> findZonesWithAvailableCapacity(@Param("minCapacity") int minCapacity);
}