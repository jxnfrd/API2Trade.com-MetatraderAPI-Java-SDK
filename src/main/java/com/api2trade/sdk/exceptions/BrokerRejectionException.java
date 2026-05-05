package com.api2trade.sdk.exceptions;

public class BrokerRejectionException extends Api2TradeException {
    private final int retcode;
    private final boolean isRetryable;

    public BrokerRejectionException(int retcode, String comment, boolean isRetryable, String rawResponse) {
        super("Broker rejection retcode=" + retcode + (comment != null ? " (comment: " + comment + ")" : ""), 200, rawResponse);
        this.retcode = retcode;
        this.isRetryable = isRetryable;
    }

    public int getRetcode() { return retcode; }
    public boolean isRetryable() { return isRetryable; }
}
