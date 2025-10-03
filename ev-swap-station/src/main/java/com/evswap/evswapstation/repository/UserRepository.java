package com.evswap.evswapstation.repository;

import com.evswap.evswapstation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
}