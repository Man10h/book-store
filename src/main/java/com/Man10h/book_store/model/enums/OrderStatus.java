package com.Man10h.book_store.model.enums;

public enum OrderStatus {

    CREATED(true, false, false),
    PAYMENT_PENDING(true, false, false),
    PAID(false, true, false),
    SHIPPING(false, false, true),
    COMPLETED(false, false, false),
    CANCELLED(false, false, false);

    private final boolean canCancel;
    private final boolean canShip;
    private final boolean canComplete;

    OrderStatus(boolean canCancel, boolean canShip, boolean canComplete) {
        this.canCancel = canCancel;
        this.canShip = canShip;
        this.canComplete = canComplete;
    }

    public boolean canCancel() {
        return canCancel;
    }

    public boolean canShip() {
        return canShip;
    }

    public boolean canComplete() {
        return canComplete;
    }
}