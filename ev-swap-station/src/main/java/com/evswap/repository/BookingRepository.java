package com.evswap.repository;

//import com.evswap.entity.Booking;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.List;
//
//public interface BookingRepository extends JpaRepository<Booking, Integer> {
//    List<Booking> findByUserUserID(Integer userId);
//    List<Booking> findByStationStationID(Integer stationId);
//    List<Booking> findByVehicleVehicleID(Integer vehicleId);
//}


import com.evswap.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // ĐÚNG: truy cập thuộc tính lồng: user.id
    List<Booking> findByUser_Id(Integer userId);

    // Nếu bạn cần check tồn tại:
    boolean existsByUser_IdAndStatus(Integer userId, String status);

    // (tuỳ chọn) theo username:
    List<Booking> findByUser_Username(String username);
}
