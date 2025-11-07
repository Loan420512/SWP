package com.evswap.service;

import com.evswap.dto.MomoQRResponse;
import com.evswap.entity.*;
import com.evswap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserSubscriptionService {

    private final UserRepository userRepo;
    private final PackagePlanRepository packageRepo;
    private final TransactionRepository txnRepo;
    private final UserSubscriptionRepository subscriptionRepo;

    /**
     * 🧾 B1: Khởi tạo thanh toán gói (tạo Transaction PENDING + QR MoMo)
     */
    @Transactional
    public MomoQRResponse generateMomoQR(Integer userId, Integer packageId) {

        // 🔍 1️⃣ Kiểm tra người dùng và gói hợp lệ
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

        PackagePlan pkg = packageRepo.findById(packageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy gói dịch vụ"));

        // 🔍 2️⃣ Kiểm tra nếu user đã có gói ACTIVE còn hạn thì không cho mua
        UserSubscription activeSub = subscriptionRepo
                .findFirstByUserIdAndStatusOrderByEndDateDesc(userId, "ACTIVE")
                .orElse(null);

        if (activeSub != null && activeSub.getEndDate().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bạn đang có gói " + activeSub.getPackagePlan().getPlanName() +
                            " còn hiệu lực đến " + activeSub.getEndDate() +
                            ". Vui lòng hủy gói hiện tại trước khi mua gói khác.");
        }

        // ⚠️ 3️⃣ Kiểm tra trùng giao dịch PENDING
        List<Transaction> existingPending = txnRepo.findByUserIdAndPackagePlanIdAndStatus(
                userId, packageId, "PENDING"
        );
        if (!existingPending.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bạn đã chọn gói này và giao dịch đang chờ thanh toán. " +
                            "Vui lòng hoàn tất thanh toán hoặc đợi hệ thống tự hủy sau 10 phút.");
        }

        // 🔹 4️⃣ Tạo Transaction ở trạng thái PENDING
        Transaction txn = new Transaction();
        txn.setUser(user);
        txn.setPackagePlan(pkg);
        txn.setTransactionType("SUBSCRIPTION");
        txn.setAmount(BigDecimal.valueOf(pkg.getPrice()));
        txn.setStatus("PENDING");
        txn.setTransactionTime(LocalDateTime.now());
        txn.setRecord("Mua gói " + pkg.getPlanName());

        if (user.getStation() != null) {
            txn.setStation(user.getStation());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Người dùng chưa thuộc trạm nào");
        }

        txnRepo.save(txn);

        // 🔹 5️⃣ Sinh mã QR thanh toán MoMo
        String receiver = "0856292376";
        String message = "US" + userId + "PK" + packageId + "TX" + txn.getId();
        BigDecimal amount = BigDecimal.valueOf(pkg.getPrice());

        String qrContent = "2|99|" + receiver + "||0|" + amount.intValue() +
                "|Thanh toan goi|" + message;
        String qrEncoded = URLEncoder.encode(qrContent, StandardCharsets.UTF_8);
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=" + qrEncoded;

        return new MomoQRResponse(qrUrl, receiver, amount, message);
    }

    /**
     * 🧾 B2: Xác nhận thanh toán thủ công (Admin/Trạm xác nhận đã nhận tiền)
     */
    @Transactional
    public Map<String, Object> confirmManualPayment(Long txnId) {

        Transaction txn = txnRepo.findById(txnId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch"));

        if (!"PENDING".equalsIgnoreCase(txn.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể xác nhận giao dịch đang PENDING");
        }

        txn.setStatus("SUCCESS");
        txn.setTransactionRef("MANUAL-" + System.currentTimeMillis());
        txn.setRecord("Thanh toán thủ công cho gói " + txn.getPackagePlan().getPlanName());
        txnRepo.save(txn);

        // 🔹 Tạo UserSubscription tương ứng
        User user = txn.getUser();
        PackagePlan pkg = txn.getPackagePlan();

        int durationDays = pkg.getDurationDays();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(durationDays);

        UserSubscription sub = new UserSubscription();
        sub.setUser(user);
        sub.setPackagePlan(pkg);
        sub.setTransaction(txn);
        sub.setStartDate(start);
        sub.setEndDate(end);
        sub.setStatus("ACTIVE");
        subscriptionRepo.save(sub);

        return Map.of(
                "message", "Xác nhận thanh toán thủ công thành công",
                "package", pkg.getPlanName(),
                "expireAt", end
        );
    }

    /**
     * 🕒 B3: Tự động huỷ các giao dịch chưa thanh toán sau 10 phút.
     * Chạy mỗi 2 phút.
     */
    @Scheduled(fixedRate = 2 * 60 * 1000)
    @Transactional
    public void autoCancelUnpaidSubscriptions() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);

        List<Transaction> expired = txnRepo
                .findAllByStatusAndTransactionTypeAndTransactionTimeBefore(
                        "PENDING", "SUBSCRIPTION", threshold);

        for (Transaction tx : expired) {
            tx.setStatus("FAILED");
            tx.setRecord("Auto-cancelled after 10 minutes without payment");
            txnRepo.save(tx);
        }

        if (!expired.isEmpty()) {
            System.out.println("⏰ Auto-cancelled " + expired.size() + " unpaid subscription transactions.");
        }
    }

    /**
     * 🧾 B4: Hủy gói đang hoạt động (người dùng chủ động hủy)
     */
    @Transactional
    public Map<String, Object> cancelActiveSubscription(Integer userId) {
        UserSubscription activeSub = subscriptionRepo
                .findFirstByUserIdAndStatusOrderByEndDateDesc(userId, "ACTIVE")
                .orElse(null);

        if (activeSub == null || activeSub.getEndDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không có gói nào đang hoạt động để hủy.");
        }

        activeSub.setStatus("REFUNDED");
        activeSub.setEndDate(LocalDateTime.now());
        subscriptionRepo.save(activeSub);

        // Cập nhật Transaction nếu có
        Transaction txn = activeSub.getTransaction();
        if (txn != null && "SUCCESS".equalsIgnoreCase(txn.getStatus())) {
            txn.setStatus("REFUNDED");
            txn.setRecord("Refund due to user cancelled subscription");
            txnRepo.save(txn);
        }

        return Map.of(
                "message", "Gói " + activeSub.getPackagePlan().getPlanName() + " đã được hủy thành công.",
                "cancelledAt", LocalDateTime.now()
        );
    }
}
