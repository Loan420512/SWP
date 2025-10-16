//package com.evswap.controller;
//
//import com.evswap.entity.User;
//import com.evswap.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/users")
//@RequiredArgsConstructor
//public class UserController {
//    private final UserService userService;
//
//    @GetMapping
//    public ResponseEntity<List<User>> getAll() {
//        return ResponseEntity.ok(userService.getAll());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<User> getById(@PathVariable Integer id) {
//        return userService.getById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @PostMapping
//    public ResponseEntity<User> create(@RequestBody User user) {
//        return ResponseEntity.ok(userService.create(user));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<User> update(@PathVariable Integer id, @RequestBody User user) {
//        return ResponseEntity.ok(userService.update(id, user));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Integer id) {
//        userService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//}

package com.evswap.controller;

import com.evswap.entity.Role;
import com.evswap.entity.User;
import com.evswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;

    // --- Lấy danh sách tất cả người dùng ---
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepo.findAll();
        return ResponseEntity.ok(users);
    }

    // --- Lấy người dùng theo ID ---
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return userRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Tạo mới người dùng ---
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // ✅ Convert role String -> Enum nếu client gửi "Admin", "Staff", "Driver"
        if (user.getRole() == null && user.getStatus() != null) {
            try {
                user.setRole(Role.from(user.getStatus())); // fallback
            } catch (Exception ignored) {}
        }
        // Nếu client gửi "role": "Admin" (string) thì vẫn an toàn nhờ @Enumerated
        if (user.getRole() == null && user.getUsername() != null) {
            try {
                user.setRole(Role.from(user.getUsername()));
            } catch (Exception ignored) {}
        }

        User saved = userRepo.save(user);
        return ResponseEntity.ok(saved);
    }

    // --- Cập nhật thông tin người dùng ---
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User newUser) {
        return userRepo.findById(id)
                .map(existing -> {
                    existing.setFullName(newUser.getFullName());
                    existing.setEmail(newUser.getEmail());
                    existing.setPhone(newUser.getPhone());
                    existing.setAddress(newUser.getAddress());
                    existing.setStatus(newUser.getStatus());

                    // ✅ Nếu client gửi role là String hoặc Enum đều xử lý được
                    if (newUser.getRole() != null) {
                        existing.setRole(newUser.getRole());
                    }

                    User updated = userRepo.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Xóa người dùng ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        if (!userRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
