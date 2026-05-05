package com.api2trade.sdk.models;

import java.util.List;

public class Models {
    
    public static class ConnectStatus {
        public boolean connected;
        public String id;
    }

    public static class RegisteredAccount {
        public String id;
        public String status;
    }

    public static class AccountSummary {
        public double balance;
        public double equity;
        public double margin;
        public double freeMargin;
        public double marginLevel;
        public String currency;
    }

    public static class Quote {
        public String symbol;
        public double bid;
        public double ask;
    }

    public static class OrderResult {
        public long ticket;
        public int retcode;
        public String comment;
    }

    public static class Position {
        public long ticket;
        public String symbol;
        public String type;
        public double volume;
        public double openPrice;
        public double stopLoss;
        public double takeProfit;
        public double profit;
        public double swap;
        public String comment;
    }

    public static class OrderHistoryItem {
        public long ticket;
        public String symbol;
        public String type;
        public double volume;
        public double openPrice;
        public double closePrice;
        public String openTime;
        public String closeTime;
        public double profit;
        public double swap;
        public double commission;
        public String comment;
    }

    public static class PaginatedHistory {
        public List<OrderHistoryItem> data;
        public int total;
        public int page;
        public int pageSize;
    }

    public static class Tick {
        public String symbol;
        public double bid;
        public double ask;
        public String type;
    }
}
