//package com.evswap.controller;
//
//import com.evswap.dto.BookRequest;
//import com.evswap.dto.BookingResponse;
//import com.evswap.service.BookingService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/bookings")
//@RequiredArgsConstructor
//public class BookingController {
//
//    private final BookingService bookingService;
//
//    /**
//     * BR1 – BOOKED: đặt lịch với tiền cọc 20% (xử lý bị động).
//     * - Server tự tính estimatedPrice nếu client không gửi/<=0.
//     */
//    @PostMapping
//    public ResponseEntity<BookingResponse> book(@RequestBody @Valid BookRequest req) {
//        var res = bookingService.bookWithPassiveDeposit(
//                req.getUserId(),
//                req.getStationId(),
//                req.getVehicleId(),
//                req.getBatteryId(),
//                req.getTimeSlot(),
//                req.getEstimatedPrice(),
//                req.getHoldMinutes() == null ? 30 : req.getHoldMinutes()
//        );
//        return ResponseEntity.ok(res);
//    }
//
//    // Bạn có thể thêm các endpoint cancel/arrive/complete… sau này.
//}

package com.evswap.controller;

import com.evswap.dto.*;
import com.evswap.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Tạo booking mới (20% cọc)",
            description = "Đặt lịch với tiền cọc 20%. Server tự tính giá nếu client không gửi.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo booking thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PostMapping
    public ResponseEntity<BookingResponse> book(@RequestBody @Valid BookRequest req) {
        return ResponseEntity.ok(
                bookingService.bookWithPassiveDeposit(
                        req.getUserId(),
                        req.getStationId(),
                        req.getVehicleId(),
                        req.getBatteryId(),
                        req.getTimeSlot(),
                        req.getEstimatedPrice(),
                        req.getHoldMinutes() == null ? 30 : req.getHoldMinutes()
                )
        );
    }

    @Operation(summary = "Xác nhận thủ công việc thanh toán đặt cọc",
        description = "Dành cho Staff/Admin, sau khi xác minh user đã chuyển tiền vào ví MoMo trạm.")
    @PostMapping("/{id}/confirm-deposit-manual")
    public ResponseEntity<BookingResponse> confirmDepositManual(@PathVariable Long id) {
        var res = bookingService.confirmDepositManual(id);
        return ResponseEntity.ok(res);
    }


    @Operation(summary = "Hủy booking", description = "Người dùng hoặc hệ thống hủy booking hiện tại.")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id,
            @RequestBody @Valid CancelRequest req
    ) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, req.getReason()));
    }

    @Operation(summary = "Đánh dấu khách đã đến trạm")
    @PostMapping("/{id}/arrive")
    public ResponseEntity<BookingResponse> arrive(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.markArrived(id));
    }

    @Operation(summary = "Hoàn tất booking sau khi đổi pin")
    @PostMapping("/{id}/complete")
    public ResponseEntity<BookingResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.markCompleted(id));
    }

    @Operation(summary = "Sinh mã QR MoMo để người dùng thanh toán")
    @GetMapping("/{id}/momo-qr")
    public ResponseEntity<MomoQRResponse> getMomoQR(@PathVariable Long id) {
        MomoQRResponse response = bookingService.generateMomoQR(id);
        return ResponseEntity.ok(response);
    }


}

