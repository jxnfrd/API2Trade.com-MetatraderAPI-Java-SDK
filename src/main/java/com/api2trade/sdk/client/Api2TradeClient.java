package com.api2trade.sdk.client;

import com.api2trade.sdk.resources.AccountsResource;
import com.api2trade.sdk.resources.HistoryResource;
import com.api2trade.sdk.resources.MarketResource;
import com.api2trade.sdk.resources.OrdersResource;
import com.api2trade.sdk.streaming.StreamingClient;
import com.api2trade.sdk.models.Models;

import java.util.List;
import java.util.function.Consumer;

public class Api2TradeClient {
    private final HttpClientWrapper http;
    private final StreamingClient streaming;

    public final AccountsResource accounts;
    public final MarketResource market;
    public final OrdersResource orders;
    public final HistoryResource history;

    public static class Builder {
        private String apiKey = System.getenv("API2TRADE_API_KEY");
        private String proUser;
        private String proPass;
        private String baseUrl = System.getenv("API2TRADE_BASE_URL") != null ? System.getenv("API2TRADE_BASE_URL") : "https://api.metatraderapi.dev";
        private String wsUrl = System.getenv("API2TRADE_WS_URL") != null ? System.getenv("API2TRADE_WS_URL") : "wss://api.metatraderapi.dev/stream";
        private int maxRetries = 3;
        private long backoffMs = 500;

        public Builder apiKey(String apiKey) { this.apiKey = apiKey; return this; }
        public Builder proAuth(String username, String password) { this.proUser = username; this.proPass = password; return this; }
        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }
        public Builder wsUrl(String wsUrl) { this.wsUrl = wsUrl; return this; }

        public Api2TradeClient build() {
            return new Api2TradeClient(this);
        }
    }

    private Api2TradeClient(Builder builder) {
        this.http = new HttpClientWrapper(builder.baseUrl, builder.apiKey, builder.proUser, builder.proPass, builder.maxRetries, builder.backoffMs);
        this.streaming = new StreamingClient(builder.wsUrl, builder.apiKey, builder.proUser, builder.proPass);

        this.accounts = new AccountsResource(this.http);
        this.market = new MarketResource(this.http);
        this.orders = new OrdersResource(this.http, builder.maxRetries, builder.backoffMs);
        this.history = new HistoryResource(this.http);
    }

    public void stream(String accountId, List<String> symbols, Consumer<Models.Tick> onTick, Consumer<Exception> onError, Runnable onConnect) {
        this.streaming.stream(accountId, symbols, onTick, onError, onConnect);
    }

    public void closeStream() {
        this.streaming.close();
    }
}
