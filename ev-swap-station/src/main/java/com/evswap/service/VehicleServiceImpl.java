package com.evswap.service;

import com.evswap.entity.User;
import com.evswap.entity.Vehicle;
import com.evswap.repository.UserRepository;
import com.evswap.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return vehicleRepository.findById(id)
                .map(existing -> {
                    existing.setVin(v.getVin());
                    existing.setVehicleModel(v.getVehicleModel());
                    existing.setBatteryType(v.getBatteryType());
                    existing.setRegisterInformation(v.getRegisterInformation());
                    return vehicleRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    @Override
    public void delete(Integer id) {
        vehicleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Vehicle registerFirstVehicle(Vehicle vehicle, String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        
        vehicle.setUser(user);
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public Vehicle addVehicle(Vehicle vehicle, String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        
        vehicle.setUser(user);
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public Vehicle updateVehicle(Integer id, Vehicle vehicle, String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        
        // Check ownership
        if (!existing.getUser().getUserID().equals(user.getUserID())) {
            throw new RuntimeException("Not authorized to update this vehicle");
        }
        
        existing.setVin(vehicle.getVin());
        existing.setVehicleModel(vehicle.getVehicleModel());
        existing.setBatteryType(vehicle.getBatteryType());
        existing.setRegisterInformation(vehicle.getRegisterInformation());
        
        return vehicleRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteVehicle(Integer id, String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        
        // Check ownership
        if (!vehicle.getUser().getUserID().equals(user.getUserID())) {
            throw new RuntimeException("Not authorized to delete this vehicle");
        }
        
        vehicleRepository.deleteById(id);
    }

    @Override
    public List<Vehicle> getByUsername(String username) {
        return vehicleRepository.findByUser_Username(username);
    }
}

