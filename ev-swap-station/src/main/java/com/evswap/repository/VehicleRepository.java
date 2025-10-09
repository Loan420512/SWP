package com.evswap.repository;

//import com.evswap.entity.Vehicle;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.List;
//import java.util.Optional;
//
//public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
//    List<Vehicle> findByUser_UserID(Integer userId);
//    Optional<Vehicle> findByVin(String vin);
//}


import com.evswap.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    // Đúng: đi theo đường thuộc tính Java Vehicle.user.id
    List<Vehicle> findByUser_Id(Integer userId);

    // Tiện ích thêm (thường dùng)
    boolean existsByVin(String vin);
    Optional<Vehicle> findByVin(String vin);

    // Nếu muốn truy theo username (tùy entity User của bạn là 'username' hay 'userName'):
    // List<Vehicle> findByUser_Username(String username);
    // hoặc nếu field đặt là 'userName' (camelCase):
    // List<Vehicle> findByUser_UserName(String userName);
}


