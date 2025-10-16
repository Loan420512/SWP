package com.evswap.entity.converter;

import com.evswap.entity.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;

@Converter(autoApply = false)
public class RoleAttributeConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role attribute) {
        return attribute == null ? null : attribute.name(); // lưu IN HOA
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Role.valueOf(dbData.trim().toUpperCase(Locale.ROOT)); // đọc case-insensitive
    }
}
