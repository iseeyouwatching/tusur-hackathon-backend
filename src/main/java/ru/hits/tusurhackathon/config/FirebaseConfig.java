//package ru.hits.tusurhackathon.config;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.messaging.FirebaseMessaging;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//
//import java.io.IOException;
//
//@Configuration
//public class FirebaseConfig {
//
//    @Bean
//    FirebaseMessaging firebaseMessaging() throws IOException {
//        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
//                new ClassPathResource("firebase-service-account.json").getInputStream());
//
//        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
//                .setCredentials(googleCredentials)
//                .build();
//
//        FirebaseApp firebaseApp;
//        if (FirebaseApp.getApps().isEmpty()) {
//            firebaseApp = FirebaseApp.initializeApp(firebaseOptions, "tusur-hackathon");
//        } else {
//            firebaseApp = FirebaseApp.getApps().get(0);
//        }
//
//        return FirebaseMessaging.getInstance(firebaseApp);
//    }
//
//}
