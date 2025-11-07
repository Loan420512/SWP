package com.evswap.service;

import com.evswap.dto.VehicleDTO;
import com.evswap.entity.User;
import com.evswap.entity.Vehicle;
import com.evswap.repository.UserRepository;
import com.evswap.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public List<Vehicle> getAll() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getById(Integer id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle save(Vehicle vehicle) {
        // ✅ Kiểm tra VIN trùng
        if (vehicleRepository.existsByVin(vehicle.getVin())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "VIN đã được đăng ký rồi. Hãy dùng VIN khác!"
            );
        }

        return vehicleRepository.save(vehicle);
    }


    // ✅ update có cả registerInformation
    public Vehicle updateFromDto(Integer id, VehicleDTO dto) {
        return vehicleRepository.findById(id)
                .map(v -> {
                    v.setVin(dto.getVin());
                    v.setVehicleModel(dto.getVehicleModel());
                    v.setBatteryType(dto.getBatteryType());
                    v.setRegisterInformation(dto.getRegisterInformation());

                    if (dto.getUserId() != null) {
                        User u = userRepository.findById(dto.getUserId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
                        v.setUser(u);
                    }
                    return vehicleRepository.save(v);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));
    }

    public void delete(Integer id) {
        vehicleRepository.deleteById(id);
    }
}
