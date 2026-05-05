package com.api2trade.sdk.client;

import com.api2trade.sdk.exceptions.Api2TradeException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HttpClientWrapper {
    private final OkHttpClient client;
    private final Gson gson;
    private final String baseUrl;
    private final String apiKey;
    private final String authHeader;
    private final int maxRetries;
    private final long backoffMs;

    public HttpClientWrapper(String baseUrl, String apiKey, String proUser, String proPass, int maxRetries, long backoffMs) {
        this.baseUrl = baseUrl.replaceAll("/$", "");
        this.apiKey = apiKey;
        this.maxRetries = maxRetries;
        this.backoffMs = backoffMs;
        this.gson = new Gson();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        if (proUser != null && proPass != null) {
            String credentials = proUser + ":" + proPass;
            this.authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
        } else {
            this.authHeader = null;
        }
    }

    public <T> T get(String endpoint, Map<String, Object> queryParams, Class<T> responseType) {
        return request("GET", endpoint, queryParams, null, responseType);
    }

    public <T> T post(String endpoint, Map<String, Object> queryParams, Object body, Class<T> responseType) {
        return request("POST", endpoint, queryParams, body, responseType);
    }

    public void delete(String endpoint, Map<String, Object> queryParams) {
        request("DELETE", endpoint, queryParams, null, Void.class);
    }

    private <T> T request(String method, String endpoint, Map<String, Object> queryParams, Object bodyObj, Class<T> responseType) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + endpoint).newBuilder();
        
        if (apiKey != null && !apiKey.isEmpty()) {
            urlBuilder.addQueryParameter("api_key", apiKey);
        }
        
        if (queryParams != null) {
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                if (entry.getValue() != null) {
                    urlBuilder.addQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Accept", "application/json");

        if (authHeader != null) {
            requestBuilder.addHeader("Authorization", authHeader);
        }

        if (bodyObj != null) {
            String jsonBody = gson.toJson(bodyObj);
            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));
            requestBuilder.method(method, body);
        } else if (method.equals("POST")) {
            requestBuilder.post(RequestBody.create("", MediaType.parse("application/json; charset=utf-8")));
        } else {
            requestBuilder.method(method, null);
        }

        Request request = requestBuilder.build();

        int attempt = 0;
        while (attempt <= maxRetries) {
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                
                if (response.isSuccessful()) {
                    if (responseType == Void.class) return null;
                    return gson.fromJson(responseBody, responseType);
                }

                if (response.code() == 429) {
                    if (attempt < maxRetries) {
                        sleep(backoffMs * (1L << attempt));
                        attempt++;
                        continue;
                    }
                    throw new Api2TradeException("Rate limit exceeded", 429, responseBody);
                } else if (response.code() >= 500) {
                    if (attempt < maxRetries) {
                        sleep(backoffMs * (1L << attempt));
                        attempt++;
                        continue;
                    }
                    throw new Api2TradeException("Server Error " + response.code(), response.code(), responseBody);
                } else {
                    throw new Api2TradeException("HTTP " + response.code(), response.code(), responseBody);
                }
            } catch (IOException e) {
                if (attempt < maxRetries) {
                    sleep(backoffMs * (1L << attempt));
                    attempt++;
                    continue;
                }
                throw new Api2TradeException("Network error", e);
            }
        }
        throw new Api2TradeException("Max retries exceeded", 0, null);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
    
    public Gson getGson() {
        return gson;
    }
}
