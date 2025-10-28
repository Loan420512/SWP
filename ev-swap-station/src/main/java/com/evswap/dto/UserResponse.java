package com.evswap.dto;

import com.evswap.entity.Role;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String username;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String status;
    private Role role;              // enum -> Jackson sẽ trả chuỗi
    private StationInfo station;    // có thể null

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StationInfo {
        private Integer id;
        private String name;
        private String address;
        private String status;
        private String contact;
    }
}
