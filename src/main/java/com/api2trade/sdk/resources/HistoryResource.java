package com.api2trade.sdk.resources;

import com.api2trade.sdk.client.HttpClientWrapper;
import com.api2trade.sdk.models.Models;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryResource {
    private final HttpClientWrapper http;

    public HistoryResource(HttpClientWrapper http) {
        this.http = http;
    }

    public List<Models.OrderHistoryItem> get(String accountId, String dateFrom, String dateTo) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", accountId);
        params.put("dateFrom", dateFrom);
        params.put("dateTo", dateTo);
        String raw = http.get("/OrderHistory", params, String.class);
        return http.getGson().fromJson(raw, new TypeToken<List<Models.OrderHistoryItem>>(){}.getType());
    }

    public Models.PaginatedHistory getPage(String accountId, String dateFrom, String dateTo, int page, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", accountId);
        params.put("dateFrom", dateFrom);
        params.put("dateTo", dateTo);
        params.put("page", page);
        params.put("pageSize", pageSize);
        return http.get("/OrderHistoryPagination", params, Models.PaginatedHistory.class);
    }
}
