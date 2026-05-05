package com.api2trade.sdk.exceptions;

public class Api2TradeException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;

    public Api2TradeException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public Api2TradeException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.responseBody = null;
    }

    public int getStatusCode() { return statusCode; }
    public String getResponseBody() { return responseBody; }
}
