package com.evswap.service;

import com.evswap.entity.Role;
import com.evswap.entity.User;
import com.evswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // 🧩 Lấy toàn bộ người dùng
    public List<User> getAll() {
        return userRepository.findAll();
    }

    // 🧩 Tìm người dùng theo ID
    public Optional<User> getById(Integer id) {
        return userRepository.findById(id);
    }

    // 🧩 Validate thông tin khi tạo mới
    private void validateNewUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên đăng nhập đã được sử dụng");
        }
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã được đăng ký");
        }
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Số điện thoại đã được đăng ký");
        }
    }

    // 🧩 Tạo mới user (chỉ cho phép tạo Driver)
    public User create(User user) {

        validateNewUser(user);

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được để trống");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRole(Role.DRIVER);
        user.setStatus("Active");

        return userRepository.save(user);
    }


    // 🧩 Cập nhật thông tin user
    public User update(Integer id, User user) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setFullName(user.getFullName());
                    existing.setPhone(user.getPhone());
                    existing.setEmail(user.getEmail());
                    existing.setAddress(user.getAddress());
                    existing.setStatus(user.getStatus());

                    // Nếu client có gửi role thì mới cập nhật
                    if (user.getRole() != null) {
                        existing.setRole(user.getRole());
                    }

                    return userRepository.save(existing);
                })
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }

    // 🧩 Xóa user
    public void delete(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }
        userRepository.deleteById(id);
    }
}
