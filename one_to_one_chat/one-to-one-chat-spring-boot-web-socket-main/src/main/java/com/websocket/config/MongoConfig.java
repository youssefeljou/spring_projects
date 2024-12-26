package com.websocket.config;


import com.websocket.user.Status;
import com.websocket.user.User;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    private final MongoTemplate mongoTemplate;

    // Constructor injection is generally recommended over field injection
    public MongoConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createDatabase() {
        // Create a new user or save a sample document to the database
        User sampleUser = new User("test" , "test" , Status.ONLINE);
        mongoTemplate.save(sampleUser); // Saves the user document to MongoDB

        System.out.println("Database 'chat_app' and collection created with sample data.");
    }
}
