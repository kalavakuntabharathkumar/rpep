package com.example.pipeline.api;

import com.example.pipeline.service.PaymentEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentEventController.class)
class PaymentEventControllerTest {
    @Autowired MockMvc mvc;
    @MockBean PaymentEventPublisher publisher;

    @Test void acceptsValidEvent() throws Exception {
        mvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON)
            .content("{\"eventId\":\"evt-1\",\"paymentId\":\"pay-1\",\"eventType\":\"AUTHORIZED\",\"amount\":12.5,\"currency\":\"USD\"}"))
            .andExpect(status().isAccepted()).andExpect(jsonPath("$.accepted").value(true));
        verify(publisher).publish(org.mockito.ArgumentMatchers.any(PaymentEventRequest.class));
    }
}
