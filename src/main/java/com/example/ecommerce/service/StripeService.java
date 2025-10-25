package com.example.ecommerce.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.publishable.key}")
    private String stripePublishableKey;

    public String getPublishableKey() {
        return stripePublishableKey;
    }

    public PaymentIntent createPaymentIntent(Long amount, String currency, String customerId) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setCustomer(customerId)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                )
                .build();

        return PaymentIntent.create(params);
    }

    public Session createCheckoutSession(List<Map<String, Object>> cartItems, String successUrl, String cancelUrl) throws StripeException {
        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl);

        // Customer parameter removed - using anonymous checkout

        // Add line items
        for (Map<String, Object> item : cartItems) {
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("usd")
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName((String) item.get("name"))
                                    .setDescription((String) item.get("description"))
                                    .addImage((String) item.get("image"))
                                    .build()
                            )
                            .setUnitAmount((long)(getDoubleValue(item.get("price")) * 100)) // Convert to cents
                            .build()
                    )
                    .setQuantity(getLongValue(item.get("quantity")))
                    .build();
            builder.addLineItem(lineItem);
        }

        return Session.create(builder.build());
    }
    
    private double getDoubleValue(Object value) {
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else {
            return Double.parseDouble(value.toString());
        }
    }
    
    private long getLongValue(Object value) {
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Double) {
            return ((Double) value).longValue();
        } else {
            return Long.parseLong(value.toString());
        }
    }
}