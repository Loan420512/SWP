package com.evswap.controller;

import com.evswap.dto.VehicleDTO;
import com.evswap.entity.User;
import com.evswap.entity.Vehicle;
import com.evswap.repository.UserRepository;
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
    private final UserRepository userRepository;

    // ✅ Convert Vehicle entity → DTO
    private static VehicleDTO toDto(Vehicle v) {
        if (v == null) return null;
        Integer uid = (v.getUser() != null) ? v.getUser().getId() : null;
        String uname = (v.getUser() != null) ? v.getUser().getFullName() : null;

        return new VehicleDTO(
                v.getId(),
                v.getVin(),
                v.getVehicleModel(),
                v.getBatteryType(),
                v.getRegisterInformation(),
                uid,
                uname
        );
    }

    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getAll() {
        var list = vehicleService.getAll().stream()
                .filter(Objects::nonNull)
                .map(VehicleController::toDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> getById(@PathVariable Integer id) {
        return vehicleService.getById(id)
                .map(VehicleController::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ POST có userId
    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody VehicleDTO dto) {
        var user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var vehicle = new Vehicle();
        vehicle.setVin(dto.getVin());
        vehicle.setVehicleModel(dto.getVehicleModel());
        vehicle.setBatteryType(dto.getBatteryType());
        vehicle.setRegisterInformation(dto.getRegisterInformation());
        vehicle.setUser(user);

        var saved = vehicleService.save(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> update(@PathVariable Integer id, @RequestBody VehicleDTO dto) {
        var updated = vehicleService.updateFromDto(id, dto);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
