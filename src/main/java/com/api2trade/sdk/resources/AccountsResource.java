package com.api2trade.sdk.resources;

import com.api2trade.sdk.client.HttpClientWrapper;
import com.api2trade.sdk.models.Models;

import java.util.HashMap;
import java.util.Map;

public class AccountsResource {
    private final HttpClientWrapper http;

    public AccountsResource(HttpClientWrapper http) {
        this.http = http;
    }

    public String register(String login, String password, String server) {
        Map<String, Object> body = new HashMap<>();
        body.put("login", login);
        body.put("password", password);
        body.put("server", server);

        Models.RegisteredAccount account = http.post("/RegisterAccount", null, body, Models.RegisteredAccount.class);
        return account.id;
    }

    public Models.ConnectStatus checkConnect(String accountId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", accountId);
        return http.get("/CheckConnect", params, Models.ConnectStatus.class);
    }

    public Models.AccountSummary summary(String accountId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", accountId);
        return http.get("/AccountSummary", params, Models.AccountSummary.class);
    }

    public void delete(String accountId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", accountId);
        http.delete("/DeleteAccount", params);
    }
}
