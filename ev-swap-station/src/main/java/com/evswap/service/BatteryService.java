package com.evswap.service;

import com.evswap.entity.*;
import com.evswap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BatteryService {

    private final BatteryRepository batteryRepo;
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;

    public List<Battery> getAll() { return batteryRepo.findAll(); }
    public Optional<Battery> getById(Integer id) { return batteryRepo.findById(id); }

    public Battery create(Battery b) { return batteryRepo.save(b); }

    public Battery update(Integer id, Battery b) {
        return batteryRepo.findById(id).map(x -> {
            x.setBatteryName(b.getBatteryName());
            x.setPrice(b.getPrice());
            x.setStatus(b.getStatus());
            x.setDetailInformation(b.getDetailInformation());
            return batteryRepo.save(x);
        }).orElseThrow(() -> new RuntimeException("Battery not found"));
    }

    public void delete(Integer id) { batteryRepo.deleteById(id); }

    // DRIVER: đăng ký pin cho xe của mình
    @Transactional
    public Battery registerBatteryForVehicle(Integer vehicleId, Battery battery, String username) {
        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        var vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe"));

        // đảm bảo xe này thuộc user hiện tại
        if (!vehicle.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền đăng ký pin cho xe này");
        }

        // Lưu battery vào DB
        Battery saved = batteryRepo.save(battery);

        System.out.println("Pin đã đăng ký cho Vehicle #" + vehicleId + " bởi user " + username);

        return saved;
    }
}
