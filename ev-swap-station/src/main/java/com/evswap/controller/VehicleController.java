package com.evswap.controller;

import com.evswap.dto.VehicleDTO;
import com.evswap.entity.User;
import com.evswap.entity.Vehicle;
import com.evswap.repository.UserRepository;
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
    private final UserRepository userRepository;

    // --- helpers ---
    private static VehicleDTO toDto(Vehicle v) {
        if (v == null) return null;
        Integer uid = (v.getUser() != null) ? v.getUser().getId() : null;
        String uname = (v.getUser() != null) ? v.getUser().getFullName() : null;
        return new VehicleDTO(v.getId(), v.getVin(), v.getVehicleModel(), v.getBatteryType(), uid, uname);
    }

    // 🚗 [1] Đăng ký xe đầu tiên (Driver)
    @PostMapping("/register-first")
    public ResponseEntity<VehicleDTO> registerFirst(Authentication auth, @RequestBody Vehicle req) {
        var user = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (vehicleService.existsByVin(req.getVin())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        req.setUser(user);
        Vehicle saved = vehicleService.save(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    // 🚗 [2] Lấy danh sách xe của user hiện tại
    @GetMapping("/my")
    public ResponseEntity<List<VehicleDTO>> myVehicles(Authentication auth) {
        var user = userRepository.findByUsername(auth.getName()).orElseThrow();
        var list = vehicleService.getByUser(user.getId()).stream()
                .map(VehicleController::toDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    // 🚗 [3] Cập nhật thông tin xe
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> update(Authentication auth,
                                             @PathVariable Integer id,
                                             @RequestBody Vehicle vehicle) {
        var user = userRepository.findByUsername(auth.getName()).orElseThrow();
        Vehicle updated = vehicleService.updateByUser(id, user.getId(), vehicle);
        return ResponseEntity.ok(toDto(updated));
    }

    // 🚗 [4] Xoá xe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Integer id) {
        var user = userRepository.findByUsername(auth.getName()).orElseThrow();
        vehicleService.deleteByUser(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
