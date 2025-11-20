package com.hcmut.voltrent.constant;

public enum SortDirection {
    ASC,
    DESC;

    public SortDirection fromValue(String value) {
        for (SortDirection direction : values()) {
            if (direction.name().equalsIgnoreCase(value)) {
                return direction;
            }
        }
        return ASC;
    }
}
