package com.api2trade.sdk.enums;

public enum OrderType {
    BUY_MARKET(0),
    SELL_MARKET(1),
    BUY_LIMIT(2),
    SELL_LIMIT(3),
    BUY_STOP(4),
    SELL_STOP(5);

    private final int value;

    OrderType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isMarket() {
        return this == BUY_MARKET || this == SELL_MARKET;
    }

    public boolean isPending() {
        return this == BUY_LIMIT || this == SELL_LIMIT || this == BUY_STOP || this == SELL_STOP;
    }
}
