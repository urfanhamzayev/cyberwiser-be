package com.phoenix_sat.phoenix_sat_backend.event.listener;

import com.phoenix_sat.phoenix_sat_backend.event.RegistrationVerificationEvent;
import com.phoenix_sat.phoenix_sat_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationVerificationEventListener {
    private final EmailService emailService;

    @Async
    @EventListener
    public void onEvent(RegistrationVerificationEvent verificationEvent) {
        emailService.sendRegistrationCompleteEmail(verificationEvent);
    }
}
