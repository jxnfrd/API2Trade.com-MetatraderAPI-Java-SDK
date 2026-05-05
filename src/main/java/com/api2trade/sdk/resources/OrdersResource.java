package com.api2trade.sdk.resources;

import com.api2trade.sdk.client.HttpClientWrapper;
import com.api2trade.sdk.enums.OrderType;
import com.api2trade.sdk.enums.RetCode;
import com.api2trade.sdk.exceptions.BrokerRejectionException;
import com.api2trade.sdk.models.Models;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.google.gson.reflect.TypeToken;

public class OrdersResource {
    private final HttpClientWrapper http;
    private final int defaultMaxRetries;
    private final long defaultBackoffMs;

    public OrdersResource(HttpClientWrapper http, int defaultMaxRetries, long defaultBackoffMs) {
        this.http = http;
        this.defaultMaxRetries = defaultMaxRetries;
        this.defaultBackoffMs = defaultBackoffMs;
    }

    public Models.OrderResult send(String accountId, String symbol, OrderType type, double volume, 
                                   Double stopLoss, Double takeProfit, Double price, String comment, 
                                   boolean autoRetry, Integer maxRetries) {
        Map<String, Object> body = new HashMap<>();
        body.put("symbol", symbol);
        body.put("operation", type.getValue());
        body.put("volume", volume);
        if (stopLoss != null) body.put("stopLoss", stopLoss);
        if (takeProfit != null) body.put("takeProfit", takeProfit);
        if (price != null) body.put("price", price);
        if (comment != null) body.put("comment", comment);

        Map<String, Object> params = new HashMap<>();
        params.put("id", accountId);

        int limit = autoRetry ? (maxRetries != null ? maxRetries : defaultMaxRetries) : 0;
        
        for (int attempt = 0; attempt <= limit; attempt++) {
            Models.OrderResult result = http.post("/OrderSend", params, body, Models.OrderResult.class);
            if (result.retcode == RetCode.SUCCESS) {
                return result;
            }

            boolean retryable = RetCode.isRetryable(result.retcode);
            if (autoRetry && retryable && attempt < limit) {
                sleep(defaultBackoffMs * (attempt + 1));
                continue;
            }
            throw new BrokerRejectionException(result.retcode, result.comment, retryable, http.getGson().toJson(result));
        }
        throw new RuntimeException("Unreachable");
    }

    public Models.OrderResult modify(String accountId, long ticket, Double stopLoss, Double takeProfit) {
        Map<String, Object> body = new HashMap<>();
        body.put("ticket", ticket);
        if (stopLoss != null) body.put("stopLoss", stopLoss);
        if (takeProfit != null) body.put("takeProfit", takeProfit);

        Map<String, Object> params = new HashMap<>();
        params.put("id", accountId);

        Models.OrderResult result = http.post("/OrderModify", params, body, Models.OrderResult.class);
        if (result.retcode != RetCode.SUCCESS) {
            throw new BrokerRejectionException(result.retcode, result.comment, false, http.getGson().toJson(result));
        }
        return result;
    }

    public Models.OrderResult close(String accountId, long ticket, Double volume) {
        Map<String, Object> body = new HashMap<>();
        body.put("ticket", ticket);
        if (volume != null) body.put("volume", volume);

        Map<String, Object> params = new HashMap<>();
        params.put("id", accountId);

        Models.OrderResult result = http.post("/OrderClose", params, body, Models.OrderResult.class);
        if (result.retcode != RetCode.SUCCESS) {
            throw new BrokerRejectionException(result.retcode, result.comment, false, http.getGson().toJson(result));
        }
        return result;
    }

    public List<Models.Position> positions(String accountId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", accountId);
        String raw = http.get("/Positions", params, String.class);
        return http.getGson().fromJson(raw, new TypeToken<List<Models.Position>>(){}.getType());
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
