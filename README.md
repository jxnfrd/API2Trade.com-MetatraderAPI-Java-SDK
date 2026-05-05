# API2Trade Java SDK

> **Official Java SDK for the <a href="https://api2trade.com" rel="dofollow">API2Trade</a> Metatrader API.**  
> A robust **API for Metatrader** designed for enterprise Java applications to seamlessly manage trading accounts worldwide.  
> Whether you are building high-frequency trading bots or integrating broker systems, our industry-leading **MT5 API** and **MT4 API** let you bypass the MetaTrader terminal entirely. Perfect for institutional developers executing trades globally with ultra-low latency.

[![Java version](https://img.shields.io/badge/Java-8%2B-blue)](https://www.java.com/)
[![License](https://img.shields.io/badge/License-MIT-lightgrey)](LICENSE)
[![API Status](https://img.shields.io/badge/API_Status-99.95%25_Uptime-brightgreen)](https://status.metatraderapi.dev/)
[![Docs](https://img.shields.io/badge/Docs-docs.metatraderapi.dev-informational)](https://docs.metatraderapi.dev/docs)

---

## Installation (Maven)

Add the following dependency to your `pom.xml` (once published, or build locally):

```xml
<dependency>
    <groupId>com.api2trade</groupId>
    <artifactId>api2trade-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## 60-Second Quickstart

```java
import com.api2trade.sdk.client.Api2TradeClient;
import com.api2trade.sdk.enums.OrderType;
import com.api2trade.sdk.models.Models;

public class Main {
    public static void main(String[] args) {
        // Initialize with your API key from app.metatraderapi.dev
        Api2TradeClient client = new Api2TradeClient.Builder()
                .apiKey("YOUR_API_KEY")
                .build();

        try {
            // 1. Register your MT4/MT5 account
            String accountId = client.accounts.register("123456", "BrokerPass", "ICMarkets-Live01");
            System.out.println("Account UUID: " + accountId);

            // 2. Check live balance
            Models.AccountSummary summary = client.accounts.summary(accountId);
            System.out.println("Balance: " + summary.balance + " " + summary.currency);

            // 3. Get a live quote
            Models.Quote quote = client.market.quote(accountId, "EURUSD");
            System.out.println("Ask: " + quote.ask + " Bid: " + quote.bid);

            // 4. Place a trade
            Models.OrderResult result = client.orders.send(
                accountId, 
                "EURUSD", 
                OrderType.BUY_MARKET, 
                0.01, 
                quote.ask - 0.0020, // Stop Loss
                quote.ask + 0.0040, // Take Profit
                null, 
                "JavaBot", 
                true, // Auto-retry
                3     // Max retries
            );
            System.out.println("Ticket: " + result.ticket);

            // 5. Close the trade
            client.orders.close(accountId, result.ticket, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

---

## Configuration via Environment Variables

The `Api2TradeClient.Builder` will automatically pick up environment variables if they are set:

```bash
export API2TRADE_API_KEY=sk-...
export API2TRADE_BASE_URL=https://api.metatraderapi.dev    # optional
export API2TRADE_WS_URL=wss://api.metatraderapi.dev/stream # optional
```

```java
// Uses environment variables automatically
Api2TradeClient client = new Api2TradeClient.Builder().build(); 
```

---

## WebSocket Streaming

Receive real-time market data directly from the broker using our low-latency streaming client.

```java
import java.util.Arrays;

client.stream(
    "YOUR_ACCOUNT_ID",
    Arrays.asList("EURUSD", "XAUUSD"),
    tick -> {
        // Fired on every price update
        System.out.println(tick.symbol + ": " + tick.bid + " / " + tick.ask);
    },
    error -> {
        System.err.println("Stream error: " + error.getMessage());
    },
    () -> {
        System.out.println("Connected to stream!");
    }
);
```

---

## Error Handling

The SDK throws specific runtime exceptions that you can catch:

```java
import com.api2trade.sdk.exceptions.BrokerRejectionException;
import com.api2trade.sdk.exceptions.Api2TradeException;

try {
    client.orders.send(accountId, "EURUSD", OrderType.BUY_MARKET, 0.01, null, null, null, null, true, 3);
} catch (BrokerRejectionException e) {
    System.out.println("Broker rejected trade (Code: " + e.getRetcode() + ")");
} catch (Api2TradeException e) {
    System.out.println("API Error: " + e.getStatusCode() + " - " + e.getMessage());
}
```

---

## Support

| Channel | Link |
|---------|------|
| 📧 Email | [support@api2trade.com](mailto:support@api2trade.com) |
| 💬 Telegram | [t.me/apisupport_en](https://t.me/apisupport_en) |
| 📖 Docs | [docs.metatraderapi.dev](https://docs.metatraderapi.dev/docs) |
| 🌐 Website | [api2trade.com](https://www.api2trade.com/) |

---

## License

MIT — see [LICENSE](LICENSE).

---

*MetaTrader®, MT4®, and MT5® are trademarks of MetaQuotes Ltd. API2Trade is an independent service and is not affiliated with MetaQuotes Ltd.*
