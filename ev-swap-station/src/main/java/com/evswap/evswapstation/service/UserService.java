package com.evswap.evswapstation.service;

import com.evswap.evswapstation.entity.User;
import com.evswap.evswapstation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> getById(Integer id) {
        return userRepository.findById(id);
    }

    public User create(User user) {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("Driver");
        }
        return userRepository.save(user);
    }

    public User update(Integer id, User user) {
        return userRepository.findById(id)
                .map(u -> {
                    u.setFullName(user.getFullName());
                    u.setPhone(user.getPhone());
                    u.setEmail(user.getEmail());
                    u.setRole(user.getRole());
                    u.setAddress(user.getAddress());
                    return userRepository.save(u);
                }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void delete(Integer id) {
        userRepository.deleteById(id);
    }
}
