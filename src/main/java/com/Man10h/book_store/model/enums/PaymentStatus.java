package com.Man10h.book_store.model.enums;

public enum PaymentStatus {

    PENDING(true, true, false),
    SUCCESS(false, false, true),
    FAILED(false, true, false),
    CANCELLED(false, false, false),
    REFUNDED(false, false, false);

    private final boolean canRetry;
    private final boolean canCancel;
    private final boolean canRefund;

    PaymentStatus(boolean canRetry, boolean canCancel, boolean canRefund) {
        this.canRetry = canRetry;
        this.canCancel = canCancel;
        this.canRefund = canRefund;
    }

    public boolean canRetry() {
        return canRetry;
    }

    public boolean canCancel() {
        return canCancel;
    }

    public boolean canRefund() {
        return canRefund;
    }
}