package com.evswap.mapper;

import com.evswap.dto.BookingResponse;
import com.evswap.entity.Booking;

public final class BookingMapper {
    private BookingMapper(){}

    public static BookingResponse toDto(Booking b){
        if (b == null) return null;
        BookingResponse r = new BookingResponse();
        r.setId(b.getId());
        r.setUserId(b.getUser()!=null? b.getUser().getId():null);
        r.setStationId(b.getStation()!=null? b.getStation().getId():null);
        r.setVehicleId(b.getVehicle()!=null? b.getVehicle().getId():null);
        r.setBatteryId(b.getBattery()!=null? b.getBattery().getId():null);
        r.setTimeDate(b.getTimeDate());
        r.setEstimatedPrice(b.getEstimatedPrice());
        r.setDepositAmount(b.getDepositAmount());
        r.setDepositStatus(b.getDepositStatus());
        r.setStatus(b.getStatus());
        r.setHoldUntil(b.getHoldUntil());
        r.setCancelReason(b.getCancelReason());
        r.setCanceledAt(b.getCanceledAt());
        return r;
    }
}
