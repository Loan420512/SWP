package com.evswap.controller;

import com.evswap.dto.VehicleDTO;
import com.evswap.entity.Vehicle;
import com.evswap.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // --- helpers ---
    private static VehicleDTO toDto(Vehicle v) {
        if (v == null) return null;
        Integer uid = (v.getUser() != null) ? v.getUser().getId() : null;
        String uname = (v.getUser() != null) ? v.getUser().getFullName() : null;
        return new VehicleDTO(
                v.getId(),
                v.getVin(),
                v.getVehicleModel(),
                v.getBatteryType(),
                uid, uname
        );
    }

    // GET /api/vehicles
    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getAll() {
        var list = vehicleService.getAll().stream()
                .filter(Objects::nonNull)
                .map(VehicleController::toDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    // GET /api/vehicles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> getById(@PathVariable Integer id) {
        return vehicleService.getById(id)
                .map(VehicleController::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/vehicles  (request vẫn nhận Vehicle như cũ cho tiện phía client)
    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody Vehicle vehicle) {
        Vehicle saved = vehicleService.save(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    // PUT /api/vehicles/{id}
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> update(@PathVariable Integer id, @RequestBody Vehicle vehicle) {
        Vehicle updated = vehicleService.update(id, vehicle);
        return ResponseEntity.ok(toDto(updated));
    }

    // DELETE /api/vehicles/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

