package com.evswap.repository;

import com.evswap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);

<<<<<<< HEAD
    // Nếu đang dùng ở chỗ khác thì cứ giữ lại:
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    // Thêm mới cho login bằng email:
    Optional<User> findByEmailIgnoreCase(String email);
=======
    // Lấy user đầu tiên nếu có nhiều user với cùng email
    @Query("SELECT u FROM User u WHERE u.email = :email ORDER BY u.userID ASC")
    Optional<User> findByEmail(@Param("email") String email);

    // Lấy tất cả user với email để debug
    List<User> findAllByEmail(String email);
>>>>>>> 5d441ebf0ec6f3b7b8c6d708001a606ed675491a

    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    Optional<User> findByUserNameOrEmail(String userName, String email);

}