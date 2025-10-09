package com.evswap.repository;

//import com.evswap.entity.Transaction;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.List;
//
//public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
//        List<Transaction> findByUser_UserID(Integer userId);
//        List<Transaction> findByStation_StationID(Integer stationId);
//        List<Transaction> findByPackagePlan_Id(Integer id);
//}


import com.evswap.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // Cách 1: Derived query theo field Java
    List<Transaction> findByUser_Id(Integer userId);

    // (tuỳ chọn) theo Station
    List<Transaction> findByStation_Id(Integer stationId);

    // (tuỳ chọn) nếu bạn có field packagePlan (quan hệ tới PackagePlans)
    List<Transaction> findByPackagePlan_Id(Integer packageId);

    // Cách 2: JPQL (nếu muốn chắc chắn, không phụ thuộc đặt tên)
    @Query("select t from Transaction t where t.user.id = :userId")
    List<Transaction> findAllByUserId(@Param("userId") Integer userId);
}

