package com.evswap.controller;

import com.evswap.dto.VehicleDTO;
import com.evswap.entity.User;
import com.evswap.entity.Vehicle;
import com.evswap.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    private static VehicleDTO toDto(Vehicle v) {
        if (v == null) return null;
        Integer uid = (v.getUser() != null) ? v.getUser().getUserID() : null;
        String uname = (v.getUser() != null) ? v.getUser().getFullName() : null;
        return new VehicleDTO(v.getId(), v.getVin(), v.getVehicleModel(), v.getBatteryType(), uid, uname);
    }

    // ✅ [1] Đăng ký xe đầu tiên sau khi đăng ký tài khoản
    @PostMapping("/register-first")
    public ResponseEntity<VehicleDTO> registerFirst(@RequestBody Vehicle vehicle, Authentication auth) {
        Vehicle saved = vehicleService.registerFirstVehicle(vehicle, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    // ✅ [2] Lấy danh sách xe của người dùng hiện tại
    @GetMapping("/my")
    public ResponseEntity<List<VehicleDTO>> getMyVehicles(Authentication auth) {
        var list = vehicleService.getByUsername(auth.getName())
                .stream().filter(Objects::nonNull)
                .map(VehicleController::toDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    // ✅ [3] Thêm xe mới
    @PostMapping("/add")
    public ResponseEntity<VehicleDTO> addVehicle(@RequestBody Vehicle vehicle, Authentication auth) {
        Vehicle saved = vehicleService.addVehicle(vehicle, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    // ✅ [4] Cập nhật thông tin xe
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> update(@PathVariable Integer id, @RequestBody Vehicle vehicle, Authentication auth) {
        Vehicle updated = vehicleService.updateVehicle(id, vehicle, auth.getName());
        return ResponseEntity.ok(toDto(updated));
    }

    // ✅ [5] Xóa xe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Authentication auth) {
        vehicleService.deleteVehicle(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
