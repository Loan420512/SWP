package com.evswap.controller;

import com.evswap.entity.User;
import com.evswap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    // ✅ Tiêm UserService (thay vì UserRepository)
    private final UserService userService;

    // --- Lấy danh sách tất cả người dùng ---
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    // --- Lấy người dùng theo ID ---
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return userService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Tạo mới người dùng (chỉ role DRIVER, status ACTIVE) ---
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User saved = userService.create(user);
        return ResponseEntity.ok(saved);
    }

    // --- Cập nhật thông tin người dùng ---
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        User updated = userService.update(id, user);
        return ResponseEntity.ok(updated);
    }

    // --- Xóa người dùng ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
