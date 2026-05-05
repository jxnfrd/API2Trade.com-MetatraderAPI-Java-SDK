package com.api2trade.sdk.enums;

import java.util.Arrays;
import java.util.List;

public class RetCode {
    public static final int SUCCESS = 0;
    public static final int REQUOTE = 10004;
    public static final int REJECTED = 10006;
    public static final int CANCELED = 10007;
    public static final int ORDER_PLACED = 10008;
    public static final int COMPLETED = 10009;
    public static final int PARTIAL_FILL = 10010;
    public static final int PROCESSING_ERROR = 10011;
    public static final int CANCELED_TIMEOUT = 10012;
    public static final int INVALID_REQUEST = 10013;
    public static final int INVALID_VOLUME = 10014;
    public static final int INVALID_PRICE = 10015;
    public static final int INVALID_STOPS = 10016;
    public static final int TRADE_DISABLED = 10017;
    public static final int MARKET_CLOSED = 10018;
    public static final int INSUFFICIENT_FUNDS = 10019;
    public static final int PRICES_CHANGED = 10020;
    public static final int NO_QUOTES = 10021;
    public static final int INVALID_EXPIRATION = 10022;
    public static final int ORDER_CHANGED = 10023;
    public static final int TOO_MANY_REQUESTS = 10024;
    public static final int NO_CHANGES = 10025;
    public static final int AUTOTRADING_DISABLED = 10026;
    public static final int AGENT_BLOCKED = 10027;
    public static final int ORDER_FROZEN = 10028;
    public static final int INVALID_FILL = 10029;
    public static final int NO_CONNECTION = 10030;
    public static final int INSUFFICIENT_RIGHTS = 10031;
    public static final int TOO_FREQUENT = 10032;
    public static final int NO_CHANGES_IN_REQUEST = 10033;
    public static final int SERVER_BUSY = 10034;
    public static final int ORDER_LOCKED = 10035;
    public static final int LONG_ONLY = 10036;
    public static final int TOO_MANY_POSITIONS = 10037;

    private static final List<Integer> RETRYABLE_CODES = Arrays.asList(
        REQUOTE, CANCELED, PROCESSING_ERROR, CANCELED_TIMEOUT, PRICES_CHANGED, SERVER_BUSY
    );

    public static boolean isRetryable(int code) {
        return RETRYABLE_CODES.contains(code);
    }
}
