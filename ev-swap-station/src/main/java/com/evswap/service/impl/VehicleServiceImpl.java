package com.evswap.service.impl;

import com.evswap.entity.User;
import com.evswap.entity.Vehicle;
import com.evswap.repository.UserRepository;
import com.evswap.repository.VehicleRepository;
import com.evswap.service.VehicleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Override
    public List<Vehicle> getAll() {
        return vehicleRepository.findAll();
    }

    @Override
    public Optional<Vehicle> getById(Integer id) {
        return vehicleRepository.findById(id);
    }

    @Override
    public Vehicle save(Vehicle v) {
        return vehicleRepository.save(v);
    }

    @Override
    public Vehicle update(Integer id, Vehicle v) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Xe không tồn tại"));
        existing.setVin(v.getVin());
        existing.setVehicleModel(v.getVehicleModel());
        existing.setBatteryType(v.getBatteryType());
        existing.setRegisterInformation(v.getRegisterInformation());
        return vehicleRepository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        vehicleRepository.deleteById(id);
    }

    // === Logic mới ===

    @Override
    @Transactional
    public Vehicle registerFirstVehicle(Vehicle vehicle, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        vehicle.setUser(user);
        if (vehicleRepository.existsByVin(vehicle.getVin())) {
            throw new RuntimeException("VIN đã tồn tại");
        }
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        vehicle.setUser(user);
        if (vehicleRepository.existsByVin(vehicle.getVin())) {
            throw new RuntimeException("VIN đã tồn tại");
        }
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle updateVehicle(Integer id, Vehicle vehicle, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Vehicle existing = vehicleRepository.findByIdAndUser(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Xe không thuộc quyền sở hữu"));
        existing.setVin(vehicle.getVin());
        existing.setVehicleModel(vehicle.getVehicleModel());
        existing.setBatteryType(vehicle.getBatteryType());
        existing.setRegisterInformation(vehicle.getRegisterInformation());
        return vehicleRepository.save(existing);
    }

    @Override
    public void deleteVehicle(Integer id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Vehicle v = vehicleRepository.findByIdAndUser(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Xe không thuộc quyền sở hữu"));
        vehicleRepository.delete(v);
    }

    @Override
    public List<Vehicle> getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return vehicleRepository.findByUser_Id(user.getId());
    }
}
