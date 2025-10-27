//package com.evswap.repository;
//
//import com.evswap.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.Optional;
//
//public interface UserRepository extends JpaRepository<User, Integer> {
//    Optional<User> findByUsername(String username);
//    boolean existsByUsername(String username);
//}

package com.evswap.repository;

import com.evswap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Nếu đang dùng ở chỗ khác thì cứ giữ lại:
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    // Thêm mới cho login bằng email:
    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByUsername(String username);
    boolean existsByEmailIgnoreCase(String email);
}
