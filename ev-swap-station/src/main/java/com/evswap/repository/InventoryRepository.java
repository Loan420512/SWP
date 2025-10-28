package com.evswap.repository;

import com.evswap.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    /**
     * Khóa bản ghi tồn kho của một (StationID, BatteryID) để chống đặt chỗ đồng thời.
     * Yêu cầu DB đã UNIQUE (StationID, BatteryID).
     */
    @Query(value = """
            SELECT TOP (1) *
            FROM Inventory WITH (UPDLOCK, HOLDLOCK, ROWLOCK)
            WHERE StationID = :stationId AND BatteryID = :batteryId
            ORDER BY InventoryID
            """, nativeQuery = true)
    Optional<Inventory> lockForUpdate(@Param("stationId") Integer stationId,
                                      @Param("batteryId") Integer batteryId);

    Optional<Inventory> findByStationIdAndBatteryId(Integer stationId, Integer batteryId);
}
