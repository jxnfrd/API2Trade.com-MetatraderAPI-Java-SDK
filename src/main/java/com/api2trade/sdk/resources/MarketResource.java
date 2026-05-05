package com.api2trade.sdk.resources;

import com.api2trade.sdk.client.HttpClientWrapper;
import com.api2trade.sdk.models.Models;

import java.util.HashMap;
import java.util.Map;

public class MarketResource {
    private final HttpClientWrapper http;

    public MarketResource(HttpClientWrapper http) {
        this.http = http;
    }

    public Models.Quote quote(String accountId, String symbol) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", accountId);
        params.put("symbol", symbol);
        return http.get("/GetQuote", params, Models.Quote.class);
    }
}
