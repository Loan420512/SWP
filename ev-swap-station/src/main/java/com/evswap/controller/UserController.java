package com.evswap.controller;

import com.evswap.dto.MomoQRResponse;
import com.evswap.entity.PackagePlan;
import com.evswap.entity.User;
import com.evswap.service.PackagePlanService;
import com.evswap.service.UserService;
import com.evswap.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserSubscriptionService userSubscriptionService;
    private final PackagePlanService packagePlanService;

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

    // 🧾 B1: Sinh mã QR thanh toán MoMo cho gói đăng ký
    @PostMapping("/{userId}/buy-package/{packageId}/qr")
    public ResponseEntity<MomoQRResponse> generateQR(
            @PathVariable Integer userId,
            @PathVariable Integer packageId) {
        return ResponseEntity.ok(userSubscriptionService.generateMomoQR(userId, packageId));
    }

    // 🧾 B2: Xác nhận thanh toán thủ công
    @PostMapping("/transactions/{txnId}/confirm-manual")
    public ResponseEntity<Map<String, Object>> confirmManualPayment(
            @PathVariable Long txnId,
            @RequestParam(required = false) Integer staffId) {
        return ResponseEntity.ok(userSubscriptionService.confirmManualPayment(txnId, staffId));
    }


    /**
     * 🧾 B3: Người dùng hủy gói đang hoạt động
     * Example: POST /api/users/6/cancel-subscription
     */
    @PostMapping("/users/{userId}/cancel-subscription")
    public ResponseEntity<Map<String, Object>> cancelSubscription(
            @PathVariable Integer userId) {

        Map<String, Object> result = userSubscriptionService.cancelActiveSubscription(userId);
        return ResponseEntity.ok(result);
    }

    // ✅ API: Lấy danh sách các gói dịch vụ
    @GetMapping("/packages")
    public ResponseEntity<List<PackagePlan>> getAllPackages() {
        return ResponseEntity.ok(packagePlanService.getAllPackages());
    }

    // ✅ API: Lấy chi tiết 1 gói cụ thể
    @GetMapping("/packages/{id}")
    public ResponseEntity<PackagePlan> getPackageById(@PathVariable Integer id) {
        return ResponseEntity.ok(packagePlanService.getById(id));
    }
}
