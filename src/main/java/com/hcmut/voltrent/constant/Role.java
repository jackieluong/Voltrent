package com.hcmut.voltrent.constant;

import lombok.Getter;

public enum Role {
    ADMIN("ADMIN"),
    USER("USER"),
    MANAGER("MANAGER"),
    GUEST("GUEST"),
    COMPANY("COMPANY");
    @Getter
    private final String value;

    Role(String value) {
        this.value = value;
    }

    public static Role fromValue(String value) {
        for (Role role : Role.values()) {
            if (role.getValue().equals(value)) {
                return role;
            }
        }
        return Role.GUEST;
    }
}
