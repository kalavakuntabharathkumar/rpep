package com.example.pipeline.api;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PaymentEventRequest(
    @NotBlank String eventId,
    @NotBlank String paymentId,
    @NotBlank String eventType,
    @NotNull @DecimalMin("0.00") BigDecimal amount,
    @NotBlank String currency
) {}
