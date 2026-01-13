package com.carrental.carservice.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Value("${MONGODB_URI:mongodb://localhost:27017/car-db}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        // Environment variable'dan MongoDB URI al
        String uri = System.getenv("SPRING_DATA_MONGODB_URI");
        if (uri == null || uri.isEmpty()) {
            uri = "mongodb://mongodb:27017/car-db"; // Default Docker hostname
        }
        System.out.println("🔧 MONGO CONFIG: Connecting to " + uri);
        return MongoClients.create(uri);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "car-db");
    }
}

