package com.evswap.repository;

//import com.evswap.entity.Feedback;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.List;
//
//public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
//    List<Feedback> findByStation_StationID(Integer stationId);
//    List<Feedback> findByUser_UserID(Integer userId);
//}


import com.evswap.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    // dùng đường thuộc tính Java: feedback.user.id
    List<Feedback> findByUser_Id(Integer userId);

    // nếu có quan hệ tới Station: feedback.station.id
    List<Feedback> findByStation_Id(Integer stationId);

    // ví dụ lọc theo rating
    List<Feedback> findByRatingBetween(Integer min, Integer max);
}

