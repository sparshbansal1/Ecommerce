package com.ecommerce.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Talks to the Razorpay payment gateway (Payment module 3.4).
 *
 * Uses the JDK's built-in HttpClient + Jackson (already on the classpath), so
 * NO extra Maven dependency / SDK is needed.
 *
 * Two modes:
 *  - REAL:  when valid test keys are set in application.properties, it creates a
 *           real Razorpay order and verifies the real payment signature. The
 *           actual Razorpay popup (cards / UPI / netbanking / wallets) opens.
 *  - DEMO:  when keys are still the placeholders, it returns a fake order id so
 *           the checkout flow can be demonstrated end-to-end without keys.
 */
@Service
public class RazorpayService {

    private static final String ORDERS_URL = "https://api.razorpay.com/v1/orders";

    @Value("${razorpay.key.id:}")
    private String keyId;

    @Value("${razorpay.key.secret:}")
    private String keySecret;

    @Value("${razorpay.currency:INR}")
    private String currency;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** True when real test keys have not been configured yet. */
    public boolean isDemoMode() {
        return keyId == null || keyId.isBlank() || keyId.contains("REPLACE")
                || keySecret == null || keySecret.isBlank() || keySecret.contains("REPLACE");
    }

    public String getKeyId() {
        return keyId;
    }

    public String getCurrency() {
        return currency;
    }

    /**
     * Create a Razorpay order for the given amount.
     *
     * @param amountPaise amount in the smallest currency unit (paise for INR)
     * @return the Razorpay order id (or a demo id when keys aren't set)
     */
    public String createOrder(long amountPaise) {
        if (isDemoMode()) {
            return "demo_order_" + System.currentTimeMillis();
        }

        try {
            String body = objectMapper.createObjectNode()
                    .put("amount", amountPaise)
                    .put("currency", currency)
                    .put("receipt", "rcpt_" + System.currentTimeMillis())
                    .put("payment_capture", true)
                    .toString();

            String auth = Base64.getEncoder()
                    .encodeToString((keyId + ":" + keySecret).getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ORDERS_URL))
                    .header("Authorization", "Basic " + auth)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode json = objectMapper.readTree(response.body());
                return json.get("id").asText();
            }
            throw new IllegalStateException(
                    "Razorpay order creation failed: HTTP " + response.statusCode() + " - " + response.body());

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("Unable to reach Razorpay: " + e.getMessage(), e);
        }
    }

    /**
     * Verify the signature returned by Razorpay after a successful payment.
     * The expected signature is HMAC-SHA256(orderId + "|" + paymentId, keySecret).
     *
     * In demo mode there is no real signature, so this always returns true.
     */
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        if (isDemoMode() || (orderId != null && orderId.startsWith("demo_order_"))) {
            return true;
        }
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            StringBuilder expected = new StringBuilder();
            for (byte b : hash) {
                expected.append(String.format("%02x", b));
            }
            return expected.toString().equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}
