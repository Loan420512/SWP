package com.evswap.repository;

//import com.evswap.entity.Inventory;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.List;
//
//public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
//    List<Inventory> findByStation_Id(Integer stationId);
//    List<Inventory> findByBattery_BatteryID(Integer batteryId);
//}


import com.evswap.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    // Cách 1: Derived query (không cần @Query) — yêu cầu Station có field Java tên 'id'
    List<Inventory> findByStation_Id(Integer stationId);

    List<Inventory> findByStation_IdAndStatus(Integer stationId, String status);
    List<Inventory> findByStatus(String status);
    List<Inventory> findByBattery_Id(Integer batteryId);
}

