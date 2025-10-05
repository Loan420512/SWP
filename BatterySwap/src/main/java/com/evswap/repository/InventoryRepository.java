package com.evswap.repository;

import com.evswap.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    List<Inventory> findByStationId(Integer stationId);
    boolean existsByStationIdAndBatteryId(Integer stationId, Integer batteryId);

    @Query("select i.status as status, count(i) as qty from Inventory i where i.station.id=:stationId group by i.status")
    List<Object[]> countByStatus(Integer stationId);
}
