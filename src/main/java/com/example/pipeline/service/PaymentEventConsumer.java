package com.example.pipeline.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;

@Service
public class PaymentEventConsumer {
    private final ObjectMapper mapper;
    private final StringRedisTemplate redis;
    private final MongoTemplate mongo;

    public PaymentEventConsumer(ObjectMapper mapper, StringRedisTemplate redis, MongoTemplate mongo) {
        this.mapper = mapper; this.redis = redis; this.mongo = mongo;
    }

    @KafkaListener(topics = "payment-events", groupId = "payment-audit")
    public void consume(String raw) throws Exception {
        JsonNode event = mapper.readTree(raw);
        String eventId = event.path("eventId").asText();
        if (eventId.isBlank()) throw new IllegalArgumentException("eventId is required");
        String key = "payment-event:seen:" + eventId;
        Boolean firstSeen = redis.opsForValue().setIfAbsent(key, "1", java.time.Duration.ofDays(7));
        if (Boolean.FALSE.equals(firstSeen)) return; // Basic duplicate-delivery guard.
        mongo.save(Map.of(
            "eventId", eventId,
            "paymentId", event.path("paymentId").asText(),
            "eventType", event.path("eventType").asText(),
            "payload", raw,
            "processedAt", Instant.now().toString()
        ), "payment_event_audit");
    }
}
