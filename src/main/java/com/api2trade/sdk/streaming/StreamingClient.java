package com.api2trade.sdk.streaming;

import com.api2trade.sdk.models.Models;
import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class StreamingClient {
    private WebSocketClient webSocketClient;
    private final String baseUrl;
    private final String apiKey;
    private final String authHeader;
    private final Gson gson = new Gson();

    public StreamingClient(String baseUrl, String apiKey, String proUser, String proPass) {
        this.baseUrl = baseUrl.replaceAll("/$", "");
        this.apiKey = apiKey;
        if (proUser != null && proPass != null) {
            this.authHeader = "Basic " + Base64.getEncoder().encodeToString((proUser + ":" + proPass).getBytes());
        } else {
            this.authHeader = null;
        }
    }

    public void stream(String accountId, List<String> symbols, Consumer<Models.Tick> onTick, Consumer<Exception> onError, Runnable onConnect) {
        String uriStr = baseUrl + "?id=" + accountId;
        if (apiKey != null && !apiKey.isEmpty()) {
            uriStr += "&api_key=" + apiKey;
        }

        try {
            Map<String, String> headers = new HashMap<>();
            if (authHeader != null) headers.put("Authorization", authHeader);

            webSocketClient = new WebSocketClient(new URI(uriStr), headers) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    if (onConnect != null) onConnect.run();
                    Map<String, Object> subMsg = new HashMap<>();
                    subMsg.put("action", "subscribe");
                    subMsg.put("symbols", symbols);
                    send(gson.toJson(subMsg));
                }

                @Override
                public void onMessage(String message) {
                    try {
                        Models.Tick tick = gson.fromJson(message, Models.Tick.class);
                        if ("quote".equals(tick.type)) {
                            if (onTick != null) onTick.accept(tick);
                        }
                    } catch (Exception e) {
                        // ignore parsing errors
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    // Handle reconnect logic here if needed
                }

                @Override
                public void onError(Exception ex) {
                    if (onError != null) onError.accept(ex);
                }
            };
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            if (onError != null) onError.accept(e);
        }
    }

    public void close() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}
