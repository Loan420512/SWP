package com.evswap.service;

import com.evswap.entity.Vehicle;
import com.evswap.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public List<Vehicle> getByUser(Integer userId) {
        return vehicleRepository.findByUser_Id(userId);
    }

    public boolean existsByVin(String vin) {
        return vehicleRepository.existsByVin(vin);
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateByUser(Integer vehicleId, Integer userId, Vehicle newData) {
        var v = vehicleRepository.findByIdAndUser(vehicleId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy xe của bạn"));
        v.setVin(newData.getVin());
        v.setVehicleModel(newData.getVehicleModel());
        v.setBatteryType(newData.getBatteryType());
        v.setRegisterInformation(newData.getRegisterInformation());
        return vehicleRepository.save(v);
    }

    public void deleteByUser(Integer vehicleId, Integer userId) {
        var v = vehicleRepository.findByIdAndUser(vehicleId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy xe của bạn"));
        vehicleRepository.delete(v);
    }
}