package com.hcmut.voltrent.constant;

public enum TransferAction {
    CONFIRM,
    CANCEL;

    public static TransferAction fromString(String action) {
        for (TransferAction a : TransferAction.values()) {
            if (a.name().equalsIgnoreCase(action)) return a;
        }
        return null;
    }
}
