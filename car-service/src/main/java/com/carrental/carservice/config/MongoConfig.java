package com.carrental.carservice.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Profile("!test") // Test ortamında devre dışı (Spring Boot Embedded MongoDB kullanır)
public class MongoConfig {

    @Value("${MONGODB_URI:mongodb://localhost:27017/car-db}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        // Environment variable'dan MongoDB URI al
        String uri = System.getenv("SPRING_DATA_MONGODB_URI");
        if (uri == null || uri.isEmpty() || uri.trim().isEmpty()) {
            throw new IllegalStateException("SPRING_DATA_MONGODB_URI environment variable is required but not set!");
        }
        uri = uri.trim(); // Başında/sonunda boşluk varsa temizle
        System.out.println("🔧 MONGO CONFIG: Connecting to " + uri);
        return MongoClients.create(uri);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "car-db");
    }
}

