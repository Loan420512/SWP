//package com.evswap.repository;
//
////import com.evswap.entity.Vehicle;
////import org.springframework.data.jpa.repository.JpaRepository;
////import java.util.List;
////import java.util.Optional;
////
////public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
////    List<Vehicle> findByUser_UserID(Integer userId);
////    Optional<Vehicle> findByVin(String vin);
////}
//
//
//import com.evswap.entity.Vehicle;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
//
//    // Nếu Vehicle có field 'user' (ManyToOne User) thì method dưới sẽ chạy
//    Optional<Vehicle> findByIdAndUser_Id(Integer id, Integer userId);
//
//    // Phòng trường hợp entity không đặt tên field là 'user' -> dùng native
//    @Query(value = "SELECT TOP 1 * FROM Vehicle WHERE VehicleID = :vehicleId AND UserID = :userId", nativeQuery = true)
//    Optional<Vehicle> findByIdAndUser(@Param("vehicleId") Integer vehicleId,
//                                      @Param("userId") Integer userId);
//
//    List<Vehicle> findByUser_Id(Integer userId);
//
//    boolean existsByVin(String vin);
//}

// src/main/java/com/evswap/repository/VehicleRepository.java
package com.evswap.repository;

import com.evswap.dto.VehicleDTO;
import com.evswap.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    // dùng ở BookingService để xác nhận xe thuộc user (native cho chắc)
    @Query(value = "SELECT TOP 1 * FROM Vehicle WHERE VehicleID=:vehicleId AND UserID=:userId", nativeQuery = true)
    Optional<Vehicle> findByIdAndUser(@Param("vehicleId") Integer vehicleId,
                                      @Param("userId") Integer userId);

    List<Vehicle> findByUser_Id(Integer userId);
    boolean existsByVin(String vin);

    // --- Projection trả DTO ---
    @Query("""
        select new com.evswap.dto.VehicleDTO(
            v.id, v.vin, v.vehicleModel, v.batteryType,
            u.id, u.fullName
        )
        from Vehicle v join v.user u
        where v.id = :id
    """)
    Optional<VehicleDTO> findDtoById(@Param("id") Integer id);

    @Query("""
        select new com.evswap.dto.VehicleDTO(
            v.id, v.vin, v.vehicleModel, v.batteryType,
            u.id, u.fullName
        )
        from Vehicle v join v.user u
        where u.id = :userId
        order by v.id desc
    """)
    List<VehicleDTO> findDtosByUserId(@Param("userId") Integer userId);
}
