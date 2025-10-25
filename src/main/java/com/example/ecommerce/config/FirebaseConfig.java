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

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore getFirestore() throws Exception {
        InputStream serviceAccountStream = null;
        
        // Try environment variable first (for production)
        String firebaseCredentials = System.getenv("FIREBASE_CREDENTIALS");
        if (firebaseCredentials != null && !firebaseCredentials.isEmpty()) {
            serviceAccountStream = new ByteArrayInputStream(firebaseCredentials.getBytes());
        } else {
            // Try local file paths for development
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
            throw new RuntimeException("Firebase credentials not found. Please set FIREBASE_CREDENTIALS environment variable or place firebase-key.json in src/main/resources/");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        return FirestoreClient.getFirestore();
    }
}
