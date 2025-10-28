package com.evswap.service;

import com.evswap.entity.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    List<Vehicle> getAll();
    Optional<Vehicle> getById(Integer id);
    Vehicle save(Vehicle v);
    Vehicle update(Integer id, Vehicle v);
    void delete(Integer id);

    // === New logic ===
    Vehicle registerFirstVehicle(Vehicle vehicle, String username);
    Vehicle addVehicle(Vehicle vehicle, String username);
    Vehicle updateVehicle(Integer id, Vehicle vehicle, String username);
    void deleteVehicle(Integer id, String username);
    List<Vehicle> getByUsername(String username);
}
