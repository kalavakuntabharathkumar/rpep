package com.example.pipeline.service;

import com.example.pipeline.api.PaymentEventRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventPublisher {
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper;
    private final MeterRegistry metrics;

    public PaymentEventPublisher(KafkaTemplate<String, String> kafka, ObjectMapper mapper, MeterRegistry metrics) {
        this.kafka = kafka; this.mapper = mapper; this.metrics = metrics;
    }
    public void publish(PaymentEventRequest event) {
        try {
            String json = mapper.writeValueAsString(event);
            kafka.send("payment-events", event.paymentId(), json);
            metrics.counter("payment.events.published", "type", event.eventType()).increment();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize payment event", e);
        }
    }
}
