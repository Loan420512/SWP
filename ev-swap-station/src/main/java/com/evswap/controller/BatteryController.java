package com.evswap.controller;

import com.evswap.entity.Battery;
import com.evswap.service.BatteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/batteries")
@RequiredArgsConstructor
public class BatteryController {

    private final BatteryService service;

    @GetMapping
    public List<Battery> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Battery> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Battery> create(@RequestBody Battery b) {
        Battery saved = service.create(b);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Battery> update(@PathVariable Integer id, @RequestBody Battery b) {
        Battery updated = service.update(id, b);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Đăng ký pin mới cho xe (chỉ liên kết logic)
    @PostMapping("/register/{vehicleId}")
    public ResponseEntity<Map<String, Object>> registerBattery(
            @PathVariable Integer vehicleId,
            @RequestBody Battery battery) {

        Battery saved = service.create(battery);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("battery", saved);
        response.put("linkedVehicleId", vehicleId);
        response.put("message", "Battery registered and linked to vehicle #" + vehicleId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
