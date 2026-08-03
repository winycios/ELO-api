package br.com.elo.eloapi.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
@ConditionalOnProperty(name = "elo.notification.push.enabled", havingValue = "true")
public class FirebaseConfiguration {

    @Bean
    public FirebaseApp firebaseApp(@Value("${elo.notification.firebase.project-id:}") String projectId, @Value("${elo.notification.firebase.credentials-path:}") String credentialsPath) throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        GoogleCredentials credentials;
        if (credentialsPath.isBlank()) {
            credentials = GoogleCredentials.getApplicationDefault();
        } else {
            try (InputStream input = Files.newInputStream(Path.of(credentialsPath))) {
                credentials = GoogleCredentials.fromStream(input);
            }
        }

        FirebaseOptions.Builder options = FirebaseOptions.builder().setCredentials(credentials);
        if (!projectId.isBlank()) {
            options.setProjectId(projectId);
        }
        return FirebaseApp.initializeApp(options.build());
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
