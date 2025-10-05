package com.evswap.service;

import com.evswap.dto.booking.BookingCreateRequest;
import com.evswap.dto.booking.BookingResponse;

public interface BookingService {
    BookingResponse create(BookingCreateRequest req, String username);
    void cancel(Integer bookingId, String username, String reason);
}
