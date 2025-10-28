package com.evswap.mapper;

import com.evswap.dto.UserResponse;
import com.evswap.entity.Station;
import com.evswap.entity.User;

public final class UserMapper {
    private UserMapper(){}

    public static UserResponse toDto(User u){
        if (u == null) return null;

        UserResponse.StationInfo st = null;
        Station s = u.getStation();
        if (s != null) {
            st = UserResponse.StationInfo.builder()
                    .id(s.getId())
                    .name(s.getStationName())
                    .address(s.getAddress())
                    .status(s.getStationStatus())
                    .contact(s.getContact())
                    .build();
        }

        return UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .fullName(u.getFullName())
                .phone(u.getPhone())
                .email(u.getEmail())
                .address(u.getAddress())
                .status(u.getStatus())
                .role(u.getRole())    // ✅ enum
                .station(st)
                .build();
    }
}
