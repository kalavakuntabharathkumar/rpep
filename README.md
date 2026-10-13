# Real-Time Payment Event Pipeline

Portfolio starter showing asynchronous payment-event ingestion with Kafka, Redis duplicate-delivery guards, MongoDB audit records, Docker Compose, and Prometheus metrics.

## Flow
1. `POST /api/events` validates and publishes an event to Kafka topic `payment-events`.
2. A Kafka consumer checks a Redis key for a previously processed `eventId`.
3. First-seen events are saved to MongoDB collection `payment_event_audit`.
4. Micrometer exposes Prometheus metrics at `/actuator/prometheus`.

## Requirements
- JDK 17+
- Maven 3.9+
- Docker Desktop / Docker Engine with Compose

## Run infrastructure
```bash
docker compose up -d
```
Start the app in another terminal:
```bash
mvn spring-boot:run
```
The service listens on http://localhost:8081. Health: `/actuator/health`.

## Publish an event
```bash
curl -X POST http://localhost:8081/api/events -H "Content-Type: application/json" -d '{"eventId":"evt-001","paymentId":"pay-001","eventType":"AUTHORIZED","amount":120.50,"currency":"INR"}'
```

## Tests
```bash
mvn test
```

## Important limitations
This is a learning/portfolio scaffold, not a complete payment system. The Redis guard and Mongo write are not atomic; a crash after marking an event seen but before saving the audit record can lose an audit entry. Production systems should use stronger idempotent storage, retries/dead-letter topics, schema validation/versioning, TLS/auth for brokers, and integration/load tests. The README's runtime throughput and latency must be measured locally before being claimed on a resume.
