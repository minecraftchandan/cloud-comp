package com.example.ecommerce.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@ConditionalOnBean(Firestore.class)
public class FirestoreService {

    @Autowired
    private Firestore firestore;

    private static final String CARTS_COLLECTION = "carts";

    public String addToCart(String username, String productId, String name, String imageUrl, double price, int quantity) throws ExecutionException, InterruptedException {
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("username", username);
        cartItem.put("productId", productId);
        cartItem.put("name", name);
        cartItem.put("image", imageUrl);
        cartItem.put("price", price);
        cartItem.put("quantity", quantity);
        cartItem.put("timestamp", FieldValue.serverTimestamp());

        ApiFuture<DocumentReference> future = firestore.collection(CARTS_COLLECTION).add(cartItem);
        return future.get().getId(); 
    }

}
