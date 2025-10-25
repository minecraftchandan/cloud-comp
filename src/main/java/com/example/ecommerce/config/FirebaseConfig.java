package com.example.ecommerce.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore getFirestore() throws Exception {
        InputStream serviceAccountStream = null;

        // ✅ Try environment variable first (Base64-encoded JSON)
        String firebaseCredentialsBase64 = System.getenv("FIREBASE_CREDENTIALS");
        if (firebaseCredentialsBase64 != null && !firebaseCredentialsBase64.isEmpty()) {
            try {
                // Decode Base64 → JSON bytes
                byte[] decodedBytes = Base64.getDecoder().decode(firebaseCredentialsBase64);
                serviceAccountStream = new ByteArrayInputStream(decodedBytes);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid Base64 in FIREBASE_CREDENTIALS environment variable", e);
            }
        } else {
            // 🔍 Fallback: try common file paths (for local development)
            String[] possiblePaths = {
                "serviceAccountKey.json",
                "src/main/resources/firebase-key.json",
                "firebase-key.json",
                "/etc/secrets/firebase-key.json"
            };

            for (String path : possiblePaths) {
                if (Files.exists(Paths.get(path))) {
                    serviceAccountStream = new FileInputStream(path);
                    break;
                }
            }
        }

        if (serviceAccountStream == null) {
            throw new RuntimeException(
                "Firebase credentials not found. " +
                "Set FIREBASE_CREDENTIALS (Base64 JSON) or place firebase-key.json in src/main/resources/"
            );
        }

        // ✅ Initialize Firebase
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        return FirestoreClient.getFirestore();
    }
}
