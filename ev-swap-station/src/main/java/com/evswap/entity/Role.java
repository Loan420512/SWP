//package com.evswap.entity;
//
//public enum Role { Driver, Staff, Admin }

package com.evswap.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Locale;

public enum Role {
    DRIVER,
    STAFF,
    ADMIN;

    @JsonCreator
    public static Role from(String value) {
        if (value == null) return null;
        String norm = value.trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(r -> r.name().equals(norm))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + value));
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}


