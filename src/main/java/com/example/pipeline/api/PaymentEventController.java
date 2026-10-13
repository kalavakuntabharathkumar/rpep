package com.example.pipeline.api;

import com.example.pipeline.service.PaymentEventPublisher;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class PaymentEventController {
    private final PaymentEventPublisher publisher;
    public PaymentEventController(PaymentEventPublisher publisher) { this.publisher = publisher; }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, Object> publish(@Valid @RequestBody PaymentEventRequest request) {
        publisher.publish(request);
        return Map.of("accepted", true, "eventId", request.eventId());
    }
}
